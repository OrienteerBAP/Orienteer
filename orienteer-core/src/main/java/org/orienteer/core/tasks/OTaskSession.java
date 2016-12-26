package org.orienteer.core.tasks;

import java.util.Date;
import java.util.Map;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.TaskManagerModule;
import com.orientechnologies.orient.core.record.impl.ODocument;

@SuppressWarnings("rawtypes")
public class OTaskSession <T extends OTaskSession>{
	
	public static final String TASK_SESSION_CLASS = "OTaskSession";
	public static final String STATUS_FIELD = "status";
	public static final String START_TIMESTAMP_FIELD = "startTs";
	public static final String FINISH_TIMESTAMP_FIELD = "finishTs";
	public static final String PROGRESS_FIELD = "progress";
	public static final String PROGRESS_CURRENT_FIELD = "progressCur";
	public static final String PROGRESS_FINAL_FIELD = "progressFin";
	public static final String BREAKABLE_FIELD = "breakable";
	public static final String TEMPORARY_FIELD = "temporary";
	public static final String THREAD_NAME_FIELD = "threadName";
	
	public enum Status{
		STOPPED,RUNNING,DETACHED
	}
	
	String sessionId;
	ODocument sessionDoc;
	String sessionClass;
	ITaskSessionCallback callback;
	
	//new session
	public OTaskSession() {
		this(TASK_SESSION_CLASS);
	}
	
	//new session
	public OTaskSession(String sessionClass) {
		this.sessionClass = sessionClass;
	}
	
	//old session
	public OTaskSession(ODocument sessionDoc) {
		this.sessionDoc = sessionDoc;
	}
	
	private void makeSessionDoc(){
		if (sessionDoc == null){
			sessionDoc = new ODocument(sessionClass);
		}
	}
	
	private void registerCallback(ITaskSessionCallback callback){
		Map<String, ITaskSessionCallback> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
		metadata.put(sessionId, callback);
	}

	private void unregisterCallback(){
		Map<String, ITaskSessionCallback> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
		metadata.remove(sessionId);
	}
	//////////////////////////////////////////////////////////////////////
	
	private void linkCallback(){
		assert(sessionDoc!=null);
		
		Map<String, ITaskSessionCallback> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
		callback = metadata.get(sessionId);
	}

	private void unlinkCallback(){
		assert(sessionDoc!=null);
		Map<String, ITaskSessionCallback> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
		callback = null;
	}
	
	//////////////////////////////////////////////////////////////////////
	private void registerSelf(){

		Map<String, Integer> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSION_KEY);
		metadata.put(sessionId, 1);
	}
	
	private void unregisterSelf() {
		Map<String, Integer> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSION_KEY);
		metadata.remove(sessionId);
	}
	//////////////////////////////////////////////////////////////////////
	public boolean isDetached() {
		assert(sessionDoc!=null);
		if(!Status.STOPPED.equals(sessionDoc.field(STATUS_FIELD))){
			Map<String, Integer> metadata = OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSION_KEY);
			return (!metadata.containsKey(sessionId)); 
		}
		return false;
	}
	
	public boolean isTemporary() {
		assert(sessionDoc!=null);
		return sessionDoc.field(TEMPORARY_FIELD);
	}
	
	//////////////////////////////////////////////////////////////////////
	public void stop(){
		if(callback!=null){
			callback.stop();
		}
	}
	//////////////////////////////////////////////////////////////////////
	//call from listener
	public T onStart() {
		makeSessionDoc();
		setField(START_TIMESTAMP_FIELD,new Date().toString());
		setField(THREAD_NAME_FIELD,Thread.currentThread().getName());
		setField(BREAKABLE_FIELD,false);
		setField(TEMPORARY_FIELD,false);
		setField(STATUS_FIELD,Status.RUNNING);
		setField(STATUS_FIELD,Status.RUNNING);
		registerSelf();
		return this.asT();
	}
	
	public void onStop() {
		unlinkCallback();
		unregisterCallback();
		unregisterSelf();
		
		if (!isTemporary()){
			setField(STATUS_FIELD,Status.STOPPED);
			end();
		}else{
			sessionDoc.delete();
		}
	}
	
	private T onProgress() {
		return this.asT();
	}
	
	public T setProgress(int progress) {
		setField(PROGRESS_FIELD,progress);
		return this.asT();
	}

	public T setCurrentProgress(long progress) {
		setField(PROGRESS_CURRENT_FIELD,progress);
		return this.asT();
	}
	
	public T incrementCurrentProgress() {
		setField(PROGRESS_CURRENT_FIELD,(long)getField(PROGRESS_CURRENT_FIELD)+1);
		return this.asT();
	}
	
	public T setFinalProgress(long progress) {
		setField(PROGRESS_FINAL_FIELD,progress);
		return this.asT();
	}
	
	public T setCallback(ITaskSessionCallback callback) {
		if (this.callback!=null){
			unregisterCallback();
		}
		if(callback!=null){
			setField(BREAKABLE_FIELD,true);
			registerCallback(callback);
		}
		this.callback = callback;
		return this.asT();
	}
	
	private T setTemporary(boolean isTemporary) {
		setField(BREAKABLE_FIELD,isTemporary);
		return this.asT();
	}
	
	public void end(){
		assert(sessionDoc!=null);
		sessionDoc.save();
	}
	//////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	protected T asT(){
		return (T) this;
	}
	//////////////////////////////////////////////////////////////////////
	protected ITaskSessionCallback getCallback() {
		if (callback==null){
			linkCallback();
		}
		return callback;
	}
	
	protected Object getField(String fieldname) {
		assert(sessionDoc!=null);
		return sessionDoc.field(fieldname);
	}

	protected void setField(String fieldname,Object value) {
		assert(sessionDoc!=null);
		sessionDoc.field(fieldname,value);
	}
	
}
