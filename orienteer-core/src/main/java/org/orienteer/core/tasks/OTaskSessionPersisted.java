package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Wrapper class for exiting {@link OTaskSessionRuntime} document in DB
 */
public class OTaskSessionPersisted extends ODocumentWrapper implements ITaskSession {
	
	public OTaskSessionPersisted(ODocument sessionDoc) {
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
	
	public OTaskSessionPersisted getOTaskSessionPersisted() {
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
		new DBClosure<Boolean>() {

			@Override
			protected Boolean execute(ODatabaseDocument db) {
				db.save(document);
				return true;
			}
		}.execute();
	}
	
	public <V> void persist(final String field, final V value) {
		if(document.getIdentity().isPersistent()) {
			new DBClosure<Boolean>() {
	
				@Override
				protected Boolean execute(ODatabaseDocument db) {
					document.field(field, value);
					db.save(document);
					return true;
				}
			}.execute();
		} else {
			document.field(field, value);
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
		setCurrentProgress(getCurrentProgress()+1);
		return this;
	}

}
