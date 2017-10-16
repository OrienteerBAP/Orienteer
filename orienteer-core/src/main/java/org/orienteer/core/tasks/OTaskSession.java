package org.orienteer.core.tasks;

import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.behavior.OTaskSessionInterruptBehavior;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Wrapper class for exiting {@link OTaskSessionRuntime} document in DB
 */
public class OTaskSession extends ODocumentWrapper implements ITaskSession {
	private static final long serialVersionUID = 1L;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		V ret =  document.field(field.fieldName());
		return ret!=null?ret:defValue;
	}
	
	public <V> void persist() {
		DBClosure.sudoSave(document);
	}
	
	public <V> void persist(final String field, final V value) {
		document.field(field, value);
		if(document.getIdentity().isPersistent()) {
			DBClosure.sudoSave(document);
		}
	}

	public void relativeChange(final String field, final Object value,final String changeCommand){
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
						if (retry>=maxRetries){
							throw e;//if all retries failed
						}
					}
				}
				document.unload();
				return true;
			}
		}.execute();
	}
	
	public void increment(final String field, final Number value){
		if(document.getIdentity().isPersistent()) {
			relativeChange(field,value,"INCREMENT "+field+"=?");
		} else {
			Number oldValue = document.field(field);
			document.field(field, oldValue.doubleValue()+value.doubleValue());
		}		
	}

	public void append(final String field, final String value){
		if(document.getIdentity().isPersistent()) {
			relativeChange(field,value,"SET "+field+"=ifnull("+field+",'').append(?)");
		} else {
			String oldValue = document.field(field);
			document.field(field, oldValue+value);
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
		persist(Field.DELETE_ON_FINISH.fieldName(), deleteOnFinish);
		return this;
	}

	@Override
	public boolean isDeleteOnFinish() {
		return getField(Field.DELETE_ON_FINISH, false);
	}

	@Override
	public ITaskSession setProgress(double progress) {
		persist(Field.PROGRESS.fieldName(), progress);
		return this;
	}

	@Override
	public double getProgress() {
		return getField(Field.PROGRESS, 0.0);
	}

	@Override
	public ITaskSession setFinalProgress(double progress) {
		persist(Field.PROGRESS_FINAL.fieldName(), progress);
		return this;
	}

	@Override
	public double getFinalProgress() {
		return getField(Field.PROGRESS_FINAL, 0.0);
	}

	@Override
	public ITaskSession setCurrentProgress(double progress) {
		persist(Field.PROGRESS_CURRENT.fieldName(), progress);
		return this;
	}

	@Override
	public double getCurrentProgress() {
		return getField(Field.PROGRESS_CURRENT, 0.0);
	}
	
	@Override
	public ITaskSession incrementCurrentProgress() {
		increment(Field.PROGRESS_CURRENT.fieldName(), 1);
		return this;
	}

}
