package org.orienteer.core.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.TaskManagerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OUserCatchPasswordHook;

/**
 * Runtime object to hold and manage session status
 */
public class OTaskSession{
	
	/**
	 * Statuses of task session
	 */
	public enum Status{
		STOPPED,STOPPING,RUNNING,DETACHED
	}

	/**
	 *
	 */
	public enum ErrorTypes{
		NONE,UNKNOWN_ERROR
	}
	
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	
	private String sessionId;
	private ODocument sessionDoc;
	private OTaskSessionUpdater sessionUpdater;
	private String sessionClass;
	private ITaskSessionCallback callback;
	private boolean deleteOnFinish;
	
	private OrienteerWebApplication app;


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
	

	public OTaskSession() {
		this(TASK_SESSION_CLASS);
	}
	
	public OTaskSession(String sessionClass) {
		this.sessionClass = sessionClass;
		app = OrienteerWebApplication.get();
	}

	private void makeSessionDoc(){
		if (sessionDoc == null){
			sessionDoc = new ODocument(sessionClass);
			sessionDoc.save(true);
			sessionDoc.getDatabase().commit();
		}
	}
	
	public String getId(){
		if (sessionId == null && sessionDoc != null){
			sessionId = sessionDoc.getIdentity().toString();
		}
		return sessionId;
	}
	
	private void registerCallback(ITaskSessionCallback callback){
		getCallbacks(app).put(getId(), callback);
	}

	private void unregisterCallback(){
		getCallbacks(app).remove(getId());
	}
	//////////////////////////////////////////////////////////////////////
	protected OTaskSessionUpdater getSessionUpdater(){
		if (sessionUpdater==null){
			sessionUpdater = new OTaskSessionUpdater(getSessionDoc(),app.getOrientDbSettings());
		}
		return sessionUpdater;
	}
	
	//////////////////////////////////////////////////////////////////////
	
	private void linkCallback(){
		assert(sessionDoc!=null);
		
		callback = getCallbacks(app).get(getId());
	}

	private void unlinkCallback(){
		assert(sessionDoc!=null);
		callback = null;
	}
	
	//////////////////////////////////////////////////////////////////////
	private void registerSelf(){
		getSessions(app).put(getId(), 1);
	}
	
	private void unregisterSelf() {
		getSessions(app).remove(getId());
	}
	//////////////////////////////////////////////////////////////////////

	public boolean isDeleteOnFinish() {
		return deleteOnFinish;
	}

	//////////////////////////////////////////////////////////////////////
	//call from listener
	public OTaskSession onStart(OTask task) {
		onStart();
		setField(Field.TASK_LINK,task.getDocument().getIdentity());
		return this;
	}
	
	public OTaskSession onStart() {
		makeSessionDoc();
		updateThread();
		setStoppable(false);
		setDeleteOnFinish(false);
		setField(Field.STATUS,Status.RUNNING);
		setField(Field.START_TIMESTAMP,getDateTimeFormat().format(new Date()));
		registerSelf();
		getSessionUpdater().start();
		return this;
	}
	
	public OTaskSession onBeforeStop() {
		setField(Field.STATUS,Status.STOPPING);
		return this;
	}
	
	public void onStop() {
		unlinkCallback();
		unregisterCallback();
		unregisterSelf();
		if (!isDeleteOnFinish()){
			setField(Field.FINISH_TIMESTAMP,getDateTimeFormat().format(new Date()));
			setField(Field.STATUS,Status.STOPPED);
			end();
			getSessionUpdater().stop();
		}else{
			getSessionUpdater().deleteSession();
		}
	}
	
	public OTaskSession onProcess() {
		return this;
	}
	
	public OTaskSession onError(ErrorTypes type,String error) {
		setField(Field.ERROR_TYPE,type);
		setField(Field.ERROR,error);
		return this;
	}

	public OTaskSession setProgress(int progress) {
		setField(Field.PROGRESS,progress);
		return this;
	}

	public OTaskSession setCurrentProgress(long progress) {
		setField(Field.PROGRESS_CURRENT,progress);
		return this;
	}
	
	public OTaskSession incrementCurrentProgress() {
		incrementField(Field.PROGRESS_CURRENT, 1);
		return this;
	}

	public OTaskSession incrementCurrentProgress(long value) {
		incrementField(Field.PROGRESS_CURRENT, value);
		return this;
	}
	
	public OTaskSession setFinalProgress(long progress) {
		setField(Field.PROGRESS_FINAL,progress);
		return this;
	}
	
	public OTaskSession setCallback(ITaskSessionCallback callback) {
		if (this.callback!=null){
			unregisterCallback();
		}
		if(callback!=null){
			setStoppable(true);
			registerCallback(callback);
		}
		this.callback = callback;
		return this;
	}
	
	public OTaskSession setDeleteOnFinish(boolean deleteOnFinish) {
		this.deleteOnFinish = deleteOnFinish;
		setField(Field.DELETE_ON_FINISH,deleteOnFinish);
		return this;
	}
	
	public OTaskSession updateThread(){
		setField(Field.THREAD_NAME,Thread.currentThread().getName());
		return this;
	}

	public void end(){
		assert(sessionDoc!=null);
		getSessionUpdater().doSave();
	}
	
	private OTaskSession setStoppable(boolean stoppable) {
		setField(Field.IS_STOPPABLE,stoppable);
		return this;
	}
	
	private SimpleDateFormat getDateTimeFormat(){
	    final SimpleDateFormat dateTimeFormatInstance = new SimpleDateFormat(DATE_TIME_FORMAT);
	    dateTimeFormatInstance.setLenient(false);
	    dateTimeFormatInstance.setTimeZone(TimeZone.getDefault());
	    return dateTimeFormatInstance;
	}

	protected static final Map<String, ITaskSessionCallback> getCallbacks() {
		return OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
	}

	private Map<String, ITaskSessionCallback> getCallbacks(Application app) {
		return app.getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
	}

	protected static final Map<String, Integer> getSessions() {
		return OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSION_KEY);
	}

	private Map<String, Integer> getSessions(Application app) {
		return app.getMetaData(TaskManagerModule.TASK_MANAGER_SESSION_KEY);
	}

	public ITaskSessionCallback getCallback() {
		if (callback==null){
			linkCallback();
		}
		return callback;
	}
	
	private ODocument getSessionDoc(){
		assert(sessionDoc!=null);
		return sessionDoc;
	}

	protected void setField(Field field,Object value) {
		getSessionUpdater().set(field.fieldName(), value);
	}

	protected void incrementField(Field field,long value) {
		getSessionUpdater().increment(field.fieldName(), value);
	}
}

