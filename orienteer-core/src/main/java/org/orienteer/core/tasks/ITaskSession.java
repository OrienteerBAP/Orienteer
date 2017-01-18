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
	
	public static final String TASK_SESSION_CLASS = "OTaskSession";
	
	/**
	 * fields of task session ODocument 
	 */
	public enum Field{
		THREAD_NAME("threadName"),
		STATUS("status"),
		TASK_LINK("task"),
		START_TIMESTAMP("startTimestamp"),
		FINISH_TIMESTAMP("finishTimestamp"),
		PROGRESS("progress"),
		PROGRESS_CURRENT("progressCurrent"),
		PROGRESS_FINAL("progressFinal"),
		IS_STOPPABLE("isStoppable"),
		DELETE_ON_FINISH("deleteOnFinish"),
		ERROR_TYPE("errorType"),
		ERROR("error");
		
		private String fieldName;
		public String fieldName(){ return fieldName;}
		private Field(String fieldName){	this.fieldName = fieldName;}
	}
	
	public ITaskSession start();
	public ITaskSession finish();
	public ITaskSession interrupt() throws Exception;
	public boolean isInterruptable();
	
	public Status getStatus();
	
	public OTaskSessionRuntime getOTaskSessionRuntime();
	public OTaskSessionPersisted getOTaskSessionPersisted();
	
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
	public ITaskSession incrementCurrentProgress();
}
