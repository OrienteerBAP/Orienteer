package org.orienteer.core.tasks;

/**
 * Interface for task sessions
 */
public interface ITaskSession {
	
	/**
	 * Statuses of task session
	 */
	public enum Status{
		NOT_STARTED, RUNNING, FINISHED, INTERRUPTED, DETACHED
	}

	/**
	 *
	 */
	public enum ErrorTypes{
		NONE,UNKNOWN_ERROR
	}
	
	
	public ITaskSession start();
	public ITaskSession finish();
	public ITaskSession interrupt() throws Exception;
	public boolean isInterruptable();
	
	public Status getStatus();
	
	public OTaskSessionRuntime<?> getOTaskSessionRuntime();
	public IOTaskSessionPersisted getOTaskSessionPersisted();
	
	public ITaskSession setCallback(ITaskSessionCallback callback);
	public ITaskSessionCallback getCallback();
	public ITaskSession setDeleteOnFinish(boolean deleteOnFinish);
	public boolean isDeleteOnFinish();
	public ITaskSession setProgress(double progress);
	public double getProgress();
	public ITaskSession setFinalProgress(double progress);
	public double getFinalProgress();
	public ITaskSession setCurrentProgress(double progress);
	public double getCurrentProgress();
	public ITaskSession incrementCurrentProgress(double increment);
	
	public default ITaskSession incrementCurrentProgress() {
		return incrementCurrentProgress(1);
	}
}
