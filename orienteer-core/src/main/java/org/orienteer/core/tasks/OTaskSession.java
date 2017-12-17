package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ORecordElement.STATUS;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.behavior.OTaskSessionInterruptBehavior;
import org.orienteer.core.widget.AbstractWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Wrapper class for exiting {@link OTaskSessionRuntime} document in DB
 */
public class OTaskSession extends ODocumentWrapper implements ITaskSession {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(OTaskSession.class);

	///////////////////////////////////////////////////////////////////////
	//OMethods
	@ClassOMethod(
		icon = FAIconType.stop, bootstrap=BootstrapType.DANGER,
		filters={@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
				@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),		
		},
		behaviors={OTaskSessionInterruptBehavior.class}
	)
	public void interrupt( IMethodEnvironmentData data){
		try {
			interrupt();
		} catch (Exception e) {
			AbstractWidget<?> widget = data.getCurrentWidget();
			if(widget!=null) {
				widget.error(widget.getLocalizer().getString("errors.session.cantinterupt", widget, Model.of(e.getMessage())));
				
			}
			LOG.error("Can't interrupt the session", e);
		}
	}
	///////////////////////////////////////////////////////////////////////
	public OTaskSession(ODocument sessionDoc) {
		super(sessionDoc);
	}
	
	public boolean isActive() {
		return OTaskManager.get().isActive(this);
	}

	public boolean isDetached() {
		return !isActive() && ITaskSession.Status.RUNNING.name().equals(getField(ITaskSession.Field.STATUS));
	}
	
	public OTaskSessionRuntime getOTaskSessionRuntime() {
		return OTaskManager.get().getTaskSession(this);
	}
	
	protected OTaskSessionRuntime checkedOTaskSession() {
		OTaskSessionRuntime ret = getOTaskSessionRuntime();
		if(ret==null) throw new IllegalStateException("OTaskSession is not available in runtime: probably it was detached");
		return ret;
	}
	
	public OTaskSession getOTaskSessionPersisted() {
		return this;
	}
	
	private <V> V getField(ITaskSession.Field field) {
		return getField(field, null);
	}
	
	private <V> V getField(ITaskSession.Field field, V defValue) {
		V ret =  getDocument().field(field.fieldName());
		return ret!=null?ret:defValue;
	}
	
	public <V> void persist() {
		sudoSave();
	}
	
	/**
	 * @deprecated use  {@link setField} instead
	 */
	@Deprecated
	public <V> void persist(final String field, final V value) {
		setField(field,value);
		/*
		document.field(field, value);
		if(document.getIdentity().isPersistent()) {
			sudoSave();
		}*/
	}
	
	private void sudoSave(){
		new DBClosure<Boolean>() {
			@Override
			protected Boolean execute(ODatabaseDocument db) {
				document.save();
				if (document.getDatabase().getTransaction().isActive()){
					document.getDatabase().commit();
				}
				return true;
			}
		}.execute();
	}

	public void atomicChange(final String field, final Object value,final String changeCommand){
		new DBClosure<Boolean>() {
			@Override
			protected Boolean execute(ODatabaseDocument db) {
				int maxRetries = 50;
				OCommandSQL command = new OCommandSQL("update "+document.getIdentity()+" "+changeCommand);
				int retry = 0;					
				while(true){
					try {
						command.execute(value);
						break;
					} catch (OConcurrentModificationException  e) {
						retry++;
						try { Thread.sleep((long) (Math.random()*150));} catch (InterruptedException e1) {}
						if (retry>=maxRetries){
							throw e;//if all retries failed
						}
					}
				}
				document.reload();
				return true;
			}
		}.execute();
	}
	
	/**
	 * Atomic set field value
	 * @param field
	 * @param value
	 */
	public <V> void setField(final String field, final V value){
		document.field(field, value);
		if(document.getIdentity().isPersistent()) {
			atomicChange(field,value,"SET "+field+"=?");
		}		
	}
	/**
	 * Atomic increment number field value
	 * @param field
	 * @param value
	 */
	public void incrementField(final String field, final Number value){
		Number oldValue = document.field(field);
		document.field(field, oldValue.doubleValue()+value.doubleValue());
		if(document.getIdentity().isPersistent()) {
			atomicChange(field,value,"INCREMENT "+field+"=?");
		}		
	}

	/**
	 * Atomic append string field value
	 * @param field
	 * @param value
	 */
	public void appendField(final String field, final String value){
		String oldValue = document.field(field);
		document.field(field, oldValue+value);
		if(document.getIdentity().isPersistent()) {
			atomicChange(field,value,"SET "+field+"=ifnull("+field+",'').append(?)");
		}		
	}

	@Override
	public ITaskSession start() {
		throw new IllegalStateException("Session can't be marked as started from persisted version");
	}
	
	@Override
	public ITaskSession finish() {
		checkedOTaskSession().finish();
		return this;
	}

	@Override
	public ITaskSession interrupt() throws Exception {
		checkedOTaskSession().interrupt();
		return this;
	}
	
	@Override
	public boolean isInterruptable() {
		OTaskSessionRuntime session = getOTaskSessionRuntime();
		return session!=null && session.isInterruptable();
	}
	
	@Override
	public Status getStatus() {
		OTaskSessionRuntime session = getOTaskSessionRuntime();
		return session!=null?session.getStatus():Status.DETACHED;
	}

	@Override
	public ITaskSession setCallback(ITaskSessionCallback callback) {
		checkedOTaskSession().setCallback(callback);
		return this;
	}

	@Override
	public ITaskSessionCallback getCallback() {
		return checkedOTaskSession().getCallback();
	}

	@Override
	public ITaskSession setDeleteOnFinish(boolean deleteOnFinish) {
		setField(Field.DELETE_ON_FINISH.fieldName(), deleteOnFinish);
		return this;
	}

	@Override
	public boolean isDeleteOnFinish() {
		return getField(Field.DELETE_ON_FINISH, false);
	}

	@Override
	public ITaskSession setProgress(double progress) {
		setField(Field.PROGRESS.fieldName(), progress);
		return this;
	}

	@Override
	public double getProgress() {
		return getField(Field.PROGRESS, 0.0);
	}

	@Override
	public ITaskSession setFinalProgress(double progress) {
		setField(Field.PROGRESS_FINAL.fieldName(), progress);
		return this;
	}

	@Override
	public double getFinalProgress() {
		return getField(Field.PROGRESS_FINAL, 0.0);
	}

	@Override
	public ITaskSession setCurrentProgress(double progress) {
		setField(Field.PROGRESS_CURRENT.fieldName(), progress);
		return this;
	}

	@Override
	public double getCurrentProgress() {
		return getField(Field.PROGRESS_CURRENT, 0.0);
	}
	
	@Override
	public ITaskSession incrementCurrentProgress() {
		incrementField(Field.PROGRESS_CURRENT.fieldName(), 1);
		return this;
	}
}
