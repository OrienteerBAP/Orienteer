package org.orienteer.core.tasks;

import java.util.Map;

import org.apache.wicket.MetaDataKey;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.TaskManagerModule;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

public class OTask {
	
	
	public static final String TASK_CLASS = "OTask";
	
	public static final String STATUS_FIELD = "status";
	public static final String DATA_FIELD = "data";
	public static final String OUT_FIELD = "out";
	public static final String PROGRESS_FIELD = "progress";
	public static final String TYPE_FIELD = "type";
	
	private ODocument taskDoc;
	private OTaskData taskData;
	private OTaskOut taskOut;
	private IRealTask realTask;
	
	public enum Status{
		STOPPED,RUNNING
	}
	
	public OTask(String type,OTaskData data) {
		taskDoc = new ODocument(TASK_CLASS);
		taskDoc.field(TYPE_FIELD,type);
		taskDoc.field(DATA_FIELD,data.getInnerData());
		taskDoc.save();
	}
	
	public OTask(String taskId) {
		taskDoc = new ODocument( new ORecordId(taskId));
	}

	public OTask(ODocument taskDoc) {
		this.taskDoc = taskDoc;
	}
	//////////////////////////////////////////////////////////////////////
	
	private void linkRealTask(){
		assert(taskDoc!=null);
		
		Map<String, IRealTask> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSIONS_KEY);
		String id = taskDoc.getIdentity().toString();
		Object type = taskDoc.field(TYPE_FIELD);
		IRealTask curRealTask = metadata.get(id);
		if (curRealTask==null){
			try {
				realTask = OTaskManager.TASK_TYPES.get(type).newInstance();
				realTask.setOTask(this);
				metadata.put(id, realTask);
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			realTask = curRealTask;
		}
	}
	
	private void unlinkRealTask(){
		assert(taskDoc!=null);
		Map<String, IRealTask> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSIONS_KEY);
		String id = taskDoc.getIdentity().toString();
		metadata.remove(id);
		realTask = null;
	}
	//////////////////////////////////////////////////////////////////////
	//call from outher interface
	public void start(){
		getRealTask().start(getData());
	}

	public void stop(){
		getRealTask().stop();
	}
	
	//////////////////////////////////////////////////////////////////////
	//call from real task
	protected void onStart() {
		assert(taskDoc!=null);
		taskDoc.field(STATUS_FIELD,Status.RUNNING);
		taskDoc.save();
	}
	
	protected void onStop() {
		assert(taskDoc!=null);
		taskDoc.field(STATUS_FIELD,Status.STOPPED);
		taskDoc.save();
		unlinkRealTask();
	}
	
	protected void onProgress(double progress){
		taskDoc.field(PROGRESS_FIELD,progress);
		taskDoc.save();
	}

	protected void onUpdateOut(String outString){
		try {
			getOut().appendOut(outString);
			taskDoc.field(OUT_FIELD,getOut().getInnerOut());
			taskDoc.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	
	
	private IRealTask getRealTask() {
		if(realTask==null) linkRealTask();

		return realTask;
	}
	
	//
	public OTaskData getData(){
		assert(taskDoc!=null);
		if (taskData==null){
			taskData = new OTaskData(taskDoc.field(DATA_FIELD));
		}
		return taskData;
	}

	//
	public OTaskOut getOut(){
		assert(taskDoc!=null);
		if (taskOut==null){
			taskOut = new OTaskOut(taskDoc.field(OUT_FIELD));
		}
		return taskOut;
	}
}
