package org.orienteer.core.tasks;

import java.util.Date;

import org.orienteer.core.dao.DAO;

/**
 * Runtime object to hold and manage session status
 * @param <P> - type of a persisted version of a session
 */
public class OTaskSessionRuntime<P extends IOTaskSessionPersisted> implements ITaskSession {
	
	private P persistedSession;
	private ITaskSessionCallback callback;
	private Status status = Status.NOT_STARTED;
	
	private OTaskSessionRuntime() {
		this((Class<? extends P>)IOTaskSessionPersisted.class);
	}
	
	public static <P extends IOTaskSessionPersisted> OTaskSessionRuntime<P> simpleSession(IOTask<P> task) {
		return new OTaskSessionRuntime<P>().init(task);
	}
	
	public OTaskSessionRuntime(Class<? extends P> persistSessionClass) {
		this(DAO.create(persistSessionClass));
	}

	public OTaskSessionRuntime(P persistedSession) {
		this.persistedSession = persistedSession;
		setStatus(Status.NOT_STARTED);
	}
	
	public OTaskSessionRuntime<P> init(IOTask<P> task) {
		if(task!=null) {
			setTask(task);
			setDeleteOnFinish(task.isAutodeleteSessions());
		}
		return this;
	}
	
	@Override
	public OTaskSessionRuntime<P> setCallback(ITaskSessionCallback callback) {
		this.callback = callback;
		return this;
	}
	
	@Override
	public ITaskSessionCallback getCallback() {
		return callback;
	}
	
	@Override
	public OTaskSessionRuntime<P> setDeleteOnFinish(boolean deleteOnFinish) {
		getOTaskSessionPersisted().setDeleteOnFinish(deleteOnFinish);
		return this;
	}

	@Override
	public boolean isDeleteOnFinish() {
		return getOTaskSessionPersisted().isDeleteOnFinish();
	}

	@Override
	public OTaskSessionRuntime<P> start() {
		getOTaskSessionPersisted().setStartTimestamp(new Date());
		getOTaskSessionPersisted().setThreadName(Thread.currentThread().getName());
		setStatus(Status.RUNNING);
		getOTaskSessionPersisted().persist();
		OTaskManager.get().register(this);
		return this;
	}

	@Override
	public OTaskSessionRuntime<P> finish() {
		if (isDeleteOnFinish()){
			delSelf();
		}else{
			getOTaskSessionPersisted().setFinishTimestamp(new Date());
			setStatus(Status.FINISHED);
			getOTaskSessionPersisted().persist();
		}
		return this;
	}
	
	private void delSelf(){
		getOTaskSessionPersisted().delete();
	}
	
	@Override
	public OTaskSessionRuntime<P> interrupt() throws Exception {
		ITaskSessionCallback callback = getCallback();
		if(callback==null) throw new IllegalStateException("Session can't be interrupted: no callback specified");
		callback.interrupt();
		getOTaskSessionPersisted().setFinishTimestamp(new Date());
		setStatus(Status.INTERRUPTED);
		return this;
	}
	
	@Override
	public boolean isInterruptable() {
		return Status.RUNNING.equals(getStatus()) && getCallback()!=null;
	}
	
	@Override
	public Status getStatus() {
		return status;
	}
	
	void setStatus(Status status) {
		this.status = status;
		getOTaskSessionPersisted().setPersistedStatus(status);
	}

	@Override
	public OTaskSessionRuntime<P> getOTaskSessionRuntime() {
		return this;
	}

	@Override
	public P getOTaskSessionPersisted() {
		return persistedSession;
	}

	@Override
	public OTaskSessionRuntime<P> setProgress(double progress) {
		getOTaskSessionPersisted().setProgress(progress);
		return this;
	}

	@Override
	public double getProgress() {
		return getOTaskSessionPersisted().getProgress();
	}

	@Override
	public OTaskSessionRuntime<P> setFinalProgress(double progress) {
		getOTaskSessionPersisted().setFinalProgress(progress);
		return this;
	}

	@Override
	public double getFinalProgress() {
		return getOTaskSessionPersisted().getFinalProgress();
	}

	@Override
	public OTaskSessionRuntime<P> setCurrentProgress(double progress) {
		getOTaskSessionPersisted().setCurrentProgress(progress);
		return this;
	}

	@Override
	public double getCurrentProgress() {
		return getOTaskSessionPersisted().getCurrentProgress();
	}
	
	@Override
	public ITaskSession incrementCurrentProgress(double increment) {
		setCurrentProgress(getCurrentProgress()+increment);
		return this;
	}

	public ITaskSession setTask(IOTask<P> oTask) {
		getOTaskSessionPersisted().setTask(oTask);
		return null;
	}

}

