package org.orienteer.core.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.TaskManagerModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OUserCatchPasswordHook;

/**
 * Base task session.
 *
 * @param <T> just for chaining.For make object should be created non-anonymous 
 * class without template definition, like "private class TaskSessionImpl extends OTaskSession\<OTaskSession\>{}"
 */
public class OTaskSession <T extends OTaskSession<T>>{
	
	/**
	 * 
	 * Statuses of task session
	 *
	 */
	public enum Status{
		STOPPED,STOPPING,RUNNING,DETACHED
	}
	
	private String sessionId;
	private ODocument sessionDoc;
	private String sessionClass;
	private ITaskSessionCallback callback;

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
		DELETE_ON_FINISH("deleteOnFinish");
		
		private String fieldName;
		public String fieldName(){ return fieldName;}
		private Field(String fieldName){	this.fieldName = fieldName;}
	}
	
	/**
	 * Register fields in db 
	 */
	public static void onInstallModule(OrienteerWebApplication app, ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(TASK_SESSION_CLASS);
		helper.oProperty(Field.THREAD_NAME.fieldName(),OType.STRING,10);
		helper.oProperty(Field.STATUS.fieldName(),OType.STRING,20);
		helper.oProperty(Field.TASK_LINK.fieldName(),OType.LINK,30).linkedClass(OTask.TASK_CLASS);
		helper.oProperty(Field.START_TIMESTAMP.fieldName(),OType.DATETIME,40);
		helper.oProperty(Field.FINISH_TIMESTAMP.fieldName(),OType.DATETIME,50);
		helper.oProperty(Field.PROGRESS.fieldName(),OType.INTEGER,60);
		helper.oProperty(Field.PROGRESS_CURRENT.fieldName(),OType.LONG,70);
		helper.oProperty(Field.PROGRESS_FINAL.fieldName(),OType.LONG,80);
		helper.oProperty(Field.IS_STOPPABLE.fieldName(),OType.BOOLEAN,90);
		helper.oProperty(Field.DELETE_ON_FINISH.fieldName(),OType.BOOLEAN,100);

	}

	public static void onInitModule(OrienteerWebApplication app, ODatabaseDocument db){
		app.getOrientDbSettings().getORecordHooks().add(OTaskSessionOnDeleteHook.class);
	}
	

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
		assert(sessionDoc!=null);
		this.sessionDoc = sessionDoc;
	}
	
	public void detachUpdate(){
		if (isDetached()){
			setField(Field.STATUS, Status.DETACHED);
			save();
		}
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
		getCallbacks().put(getId(), callback);
	}

	private void unregisterCallback(){
		getCallbacks().remove(getId());
	}
	//////////////////////////////////////////////////////////////////////
	
	private void linkCallback(){
		assert(sessionDoc!=null);
		
		callback = getCallbacks().get(getId());
	}

	private void unlinkCallback(){
		assert(sessionDoc!=null);
		callback = null;
	}
	
	//////////////////////////////////////////////////////////////////////
	private void registerSelf(){
		getSessions().put(getId(), 1);
	}
	
	private void unregisterSelf() {
		getSessions().remove(getId());
	}
	//////////////////////////////////////////////////////////////////////
	public boolean isDetached() {
		if(Status.RUNNING.name().equals(getField(Field.STATUS))){
			return (!getSessions().containsKey(getId())); 
		}
		return false;
	}
	
	public boolean isDeleteOnFinish() {
		Object result  = getField(Field.DELETE_ON_FINISH);
		if (result != null)
			return (Boolean)result;
		return false;
	}
	
	public boolean isStoppable(){
		Object isStoppable  = getField(Field.IS_STOPPABLE);
		Object status  = getField(Field.STATUS);
		if (isStoppable != null && status!=null){
			if(Status.RUNNING.name().equals(status)){
				return (Boolean)isStoppable;
			}
		}
		return false;
	}	
	//////////////////////////////////////////////////////////////////////
	//call from listener
	public T onStart() {
		makeSessionDoc();
		setField(Field.THREAD_NAME,Thread.currentThread().getName());
		setField(Field.IS_STOPPABLE,false);
		setField(Field.DELETE_ON_FINISH,false);
		setField(Field.STATUS,Status.RUNNING);
		setField(Field.START_TIMESTAMP,getDateTimeFormat().format(new Date()));
		registerSelf();
		return this.asT();
	}
	
	public T onBeforeStop() {
		setField(Field.STATUS,Status.STOPPING);
		return this.asT();
	}
	
	public void onStop() {
		unlinkCallback();
		unregisterCallback();
		unregisterSelf();
		if (!isDeleteOnFinish()){
			setField(Field.FINISH_TIMESTAMP,getDateTimeFormat().format(new Date()));
			setField(Field.STATUS,Status.STOPPED);
			end();
		}else{
			sessionDoc.delete();
		}
	}
	
	public T onProcess() {
		return this.asT();
	}
	
	public T onError() {
		return this.asT();
	}

	public T setProgress(int progress) {
		setField(Field.PROGRESS,progress);
		return this.asT();
	}

	public T setCurrentProgress(long progress) {
		setField(Field.PROGRESS_CURRENT,progress);
		return this.asT();
	}
	
	public T incrementCurrentProgress() {
		Object oldProgress = getField(Field.PROGRESS_CURRENT);
		if (oldProgress==null){
			setField(Field.PROGRESS_CURRENT,1);
		}else{
			setField(Field.PROGRESS_CURRENT,(Long)oldProgress+1);
		}
		return this.asT();
	}
	
	public T setFinalProgress(long progress) {
		setField(Field.PROGRESS_FINAL,progress);
		return this.asT();
	}
	
	public T setCallback(ITaskSessionCallback callback) {
		if (this.callback!=null){
			unregisterCallback();
		}
		if(callback!=null){
			setField(Field.IS_STOPPABLE,true);
			registerCallback(callback);
		}
		this.callback = callback;
		return this.asT();
	}
	
	public T setDeleteOnFinish(boolean deleteOnFinish) {
		setField(Field.DELETE_ON_FINISH,deleteOnFinish);
		return this.asT();
	}
	
	public void end(){
		assert(sessionDoc!=null);
		sessionDoc.save();
	}
	
	private void save(){
		assert(sessionDoc!=null);
		sessionDoc.save();
	}
	
	//////////////////////////////////////////////////////////////////////

	private SimpleDateFormat getDateTimeFormat(){
		return sessionDoc.getDatabase().getStorage().getConfiguration().getDateTimeFormatInstance();
	}

	@SuppressWarnings("unchecked")
	protected T asT(){
		return (T) this;
	}
	//////////////////////////////////////////////////////////////////////
	protected Map<String, ITaskSessionCallback> getCallbacks() {
		return OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_CALLBACK_KEY);
	}

	protected Map<String, Integer> getSessions() {
		return OrienteerWebApplication.get().getMetaData(TaskManagerModule.TASK_MANAGER_SESSION_KEY);
	}
	//////////////////////////////////////////////////////////////////////
	public ITaskSessionCallback getCallback() {
		if (callback==null){
			linkCallback();
		}
		return callback;
	}
	
	protected ODocument getSessionDoc(){
		assert(sessionDoc!=null);
		return sessionDoc;
	}

	protected Object getField(Field field) {
		assert(sessionDoc!=null);
		return sessionDoc.field(field.fieldName());
	}

	protected void setField(Field field,Object value) {
		assert(sessionDoc!=null);
		sessionDoc.field(field.fieldName(),value);
	}
	
	@Deprecated
	protected Object getField(String fieldname) {
		assert(sessionDoc!=null);
		return sessionDoc.field(fieldname);
	}

	@Deprecated
	protected void setField(String fieldname,Object value) {
		assert(sessionDoc!=null);
		sessionDoc.field(fieldname,value);
	}
	
}
