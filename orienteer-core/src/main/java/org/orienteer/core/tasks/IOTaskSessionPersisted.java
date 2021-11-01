package org.orienteer.core.tasks;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;

import java.util.Date;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOClass;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.behavior.OTaskSessionInterruptBehavior;
import org.orienteer.transponder.annotation.DefaultValue;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.IODocumentWrapper;
import org.orienteer.transponder.orientdb.OrientDBProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for exiting {@link OTaskSessionRuntime} document in DB
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOTaskSessionPersisted.CLASS_NAME)
@OrienteerOClass(nameProperty = "threadName")
public interface IOTaskSessionPersisted extends ITaskSession, IODocumentWrapper {
	static final Logger LOG = LoggerFactory.getLogger(IOTaskSessionPersisted.class);
	public static final String CLASS_NAME = "OTaskSession";
	
	public String getThreadName();
	public void setThreadName(String value);
	
	@EntityProperty("status")
	public Status getPersistedStatus();
	@EntityProperty("status")
	public IOTaskSessionPersisted setPersistedStatus(Status value);
	
	@EntityProperty(inverse = "sessions")
	public IOTask<? extends IOTaskSessionPersisted> getTask();
	public IOTaskSessionPersisted setTask(IOTask<? extends IOTaskSessionPersisted> value);
	
	@OrientDBProperty(type = OType.DATETIME)
	public Date getStartTimestamp();
	public IOTaskSessionPersisted setStartTimestamp(Date value);
	
	@OrientDBProperty(type = OType.DATETIME)
	public Date getFinishTimestamp();
	public IOTaskSessionPersisted setFinishTimestamp(Date value);
	
	@Override
	public double getProgress();
	@Override
	public IOTaskSessionPersisted setProgress(double value);
	
	@Override
	@EntityProperty("progressCurrent")
	@OrientDBProperty(notNull = false)
	@DefaultValue("0.0")
	public double getCurrentProgress();
	@Override
	@EntityProperty("progressCurrent")
	@OrientDBProperty(notNull = false)
	public IOTaskSessionPersisted setCurrentProgress(double value);
	
	@Override
	@EntityProperty("progressFinal")
	@OrientDBProperty(notNull = false)
	@DefaultValue("0.0")
	public double getFinalProgress();
	@Override
	@EntityProperty("progressFinal")
	@OrientDBProperty(notNull = false)
	public IOTaskSessionPersisted setFinalProgress(double value);
	
	@EntityProperty("isStoppable")
	@OrientDBProperty(notNull = false)
	@DefaultValue("false")
	public boolean isStopable();
	public IOTaskSessionPersisted setStoppable(boolean value);
	
	@Override
	@OrientDBProperty(notNull = false, defaultValue = "false")
	@DefaultValue("false")
	public boolean isDeleteOnFinish();
	@Override
	public IOTaskSessionPersisted setDeleteOnFinish(boolean deleteOnFinish);
	
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_TEXTAREA)
	public String getOutput();
	public IOTaskSessionPersisted setOutput(String value);
	
	public default IOTaskSessionPersisted appendOutput(String add) {
		String out = getOutput();
		setOutput(out!=null?out+"\n"+add:add);
		return this;
	}
	
	public Integer getErrorType();
	public IOTaskSessionPersisted setErrorType(Integer value);
	
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_TEXTAREA)
	public String getError();
	public IOTaskSessionPersisted setError(String value);
	
	public default IOTaskSessionPersisted appendError(String add) {
		String error = getError();
		setOutput(error!=null?error+"\n"+add:add);
		return this;
	}
	
	///////////////////////////////////////////////////////////////////////
	//OMethods
	@OMethod(
		icon = FAIconType.stop, bootstrap=BootstrapType.DANGER,
		filters={@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
				@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),		
		},
		behaviors={OTaskSessionInterruptBehavior.class}
	)
	public default void interrupt( IMethodContext ctx){
		try {
			interrupt();
		} catch (Exception e) {
			ctx.showFeedback(FeedbackMessage.INFO, "errors.session.cantinterupt", Model.of(e.getMessage()));
			LOG.error("Can't interrupt the session", e);
		}
	}
	
	public default boolean isActive() {
		return OTaskManager.get().isActive(this);
	}

	public default boolean isDetached() {
		return !isActive() && ITaskSession.Status.RUNNING.equals(getPersistedStatus());
	}
	
	@Override
	public default OTaskSessionRuntime<?> getOTaskSessionRuntime() {
		return OTaskManager.get().getTaskSession(this);
	}
	
	public default OTaskSessionRuntime<?> checkedOTaskSession() {
		OTaskSessionRuntime<?> ret = getOTaskSessionRuntime();
		if(ret==null) throw new IllegalStateException("OTaskSession is not available in runtime: probably it was detached");
		return ret;
	}
	
	@Override
	public default IOTaskSessionPersisted getOTaskSessionPersisted() {
		return this;
	}
	
	@DAOHandler(SudoMethodHandler.class)
	public default void persist() {
		save();
	}

	@Override
	public default ITaskSession start() {
		throw new IllegalStateException("Session can't be marked as started from persisted version");
	}
	
	@Override
	public default ITaskSession finish() {
		checkedOTaskSession().finish();
		return this;
	}
	
	@DAOHandler(SudoMethodHandler.class)
	public default void delete() {
		getDocument().delete();
	}

	@Override
	public default ITaskSession interrupt() throws Exception {
		checkedOTaskSession().interrupt();
		return this;
	}
	
	@Override
	public default boolean isInterruptable() {
		OTaskSessionRuntime<?> session = getOTaskSessionRuntime();
		return session!=null && session.isInterruptable();
	}
	
	@Override
	public default Status getStatus() {
		OTaskSessionRuntime<?> session = getOTaskSessionRuntime();
		return session!=null?session.getStatus():Status.DETACHED;
	}

	@Override
	public default ITaskSession setCallback(ITaskSessionCallback callback) {
		checkedOTaskSession().setCallback(callback);
		return this;
	}

	@Override
	public default ITaskSessionCallback getCallback() {
		return checkedOTaskSession().getCallback();
	}

	@Override
	public default IOTaskSessionPersisted incrementCurrentProgress(double increment) {
		setCurrentProgress(getCurrentProgress()+increment);
		return this;
	}
}
