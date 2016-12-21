package org.orienteer.core.tasks;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OTask {
	
	public static final String TASK_CLASS = "OTask";
	
	public static final String STATUS_FIELD = "status";
	public static final String DATA_FIELD = "data";
	public static final String OUT_FIELD = "out";
	public static final String PROGRESS_FIELD = "progress";
	
	private ODocument innerTask;
	private OTaskData taskData;
	private OTaskOut taskOut;
	private ITaskListener taskListener;
	
	public enum Status{
		STOPPED,RUNNING
	}
	
	//new task
	public OTask() {
		innerTask = new ODocument(TASK_CLASS);
		innerTask.save();
	}
	
	//old task
	public OTask(String taskId) {
		innerTask = new ODocument( new ORecordId(taskId));
	}

	//old initialized task
	public OTask(ODocument task) {
		innerTask = task;
	}
	
	//////////////////////////////////////////////////////////////////////
	//call from outher interface
	public void stop(){
		taskListener.stop();
	}
	
	//////////////////////////////////////////////////////////////////////
	//call from ITaskListener
	protected void onStart() {
		assert(innerTask!=null);
		innerTask.field(STATUS_FIELD,Status.RUNNING);
		innerTask.save();
	}
	
	protected void onStop() {
		assert(innerTask!=null);
		innerTask.field(STATUS_FIELD,Status.STOPPED);
		innerTask.save();
	}
	
	protected void onProgress(double progress){
		innerTask.field(PROGRESS_FIELD,progress);
		innerTask.save();
	}
	
	//////////////////////////////////////////////////////////////////////
	
	

	//
	public OTaskData getData(){
		assert(innerTask!=null);
		if (taskData==null){
			taskData = new OTaskData(innerTask.field(DATA_FIELD));
		}
		return taskData;
	}

	//
	public OTaskOut getOut(){
		assert(innerTask!=null);
		if (taskOut==null){
			taskOut = new OTaskOut(innerTask.field(OUT_FIELD));
		}
		return taskOut;
	}
	
	public void setTaskListener(ITaskListener taskListener) {
		this.taskListener = taskListener;
	}
	
}
