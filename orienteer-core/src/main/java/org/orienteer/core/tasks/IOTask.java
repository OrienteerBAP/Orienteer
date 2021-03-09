package org.orienteer.core.tasks;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAODefaultValue;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.web.ODocumentPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Base task for tasks
 * @param <P> - type of a persisted version of a session
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOTask.CLASS_NAME, 
			isAbstract = true,
            displayable = {"name", "description"})
public interface  IOTask<P extends IOTaskSessionPersisted> extends IODocumentWrapper {
	public static final Logger LOG = LoggerFactory.getLogger(IOTask.class);
	public static final String CLASS_NAME = "OTask";

	public String getName();
	
	public String getDescription();
	
	@DAOField(defaultValue = "false")
	@DAODefaultValue("false")
	public boolean isAutodeleteSessions();
	public IOTask<P> setAutodeleteSessions(boolean value);
	
	@DAOField(inverse = "task", visualization = UIVisualizersRegistry.VISUALIZER_TABLE)
	public List<IOTaskSessionPersisted> getSessions();
	
	public OTaskSessionRuntime<P> startNewSession();
	
	@OMethod(
			icon = FAIconType.play, bootstrap=BootstrapType.SUCCESS,titleKey="task.command.start",
			filters={@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
					@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),		
			},
			behaviors={}
		)
	public default void startNewSession( IMethodContext ctx){
		OTaskSessionRuntime<?> runtime = null;
		try {
			runtime = startNewSession();
		} catch (Exception e) {
			ctx.showFeedback(FeedbackMessage.INFO, "errors.task.cantstart", Model.of(e.getMessage()));
			LOG.error("Can't start new task", e);
		}
		if(runtime!=null && !isAutodeleteSessions()) {
			throw new RestartResponseException(ODocumentPage.class, ODocumentPage.getPageParameters(runtime.getOTaskSessionPersisted().getDocument()));
		}
	}
}
