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
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Runtime object to hold and manage session status
 */
public class OTaskSessionRuntime implements ITaskSession{
	
	private OTaskSessionPersisted persistedSession;
	private ITaskSessionCallback callback;
	private Status status = Status.NOT_STARTED;
	
	public OTaskSessionRuntime() {
		this(TASK_SESSION_CLASS);
	}
	
	public OTaskSessionRuntime(String sessionClass) {
		this(sessionClass,false);
	}

	public OTaskSessionRuntime(String sessionClass, boolean forceSave) {
		persistedSession = new OTaskSessionPersisted(new ODocument(sessionClass));
		setStatus(Status.NOT_STARTED);
		if (forceSave){
			persistedSession.save().getDocument().getDatabase().commit();
		}
	}
	
	@Override
	public OTaskSessionRuntime setCallback(ITaskSessionCallback callback) {
		this.callback = callback;
		return this;
	}
	
	@Override
	public ITaskSessionCallback getCallback() {
		return callback;
	}
	
	@Override
	public OTaskSessionRuntime setDeleteOnFinish(boolean deleteOnFinish) {
		getOTaskSessionPersisted().setDeleteOnFinish(deleteOnFinish);
		return this;
	}

	@Override
	public boolean isDeleteOnFinish() {
		return getOTaskSessionPersisted().isDeleteOnFinish();
	}

	@Override
	public OTaskSessionRuntime start() {
		getOTaskSessionPersisted().getDocument().field(Field.START_TIMESTAMP.fieldName(), new Date());
		getOTaskSessionPersisted().getDocument().field(Field.THREAD_NAME.fieldName(), Thread.currentThread().getName());
		setStatus(Status.RUNNING);
		getOTaskSessionPersisted().persist();
		OTaskManager.get().register(this);
		return this;
	}

	@Override
	public OTaskSessionRuntime finish() {
		if (isDeleteOnFinish()){
			delSelf();
		}else{
			getOTaskSessionPersisted().getDocument().field(Field.FINISH_TIMESTAMP.fieldName(), new Date());
			setStatus(Status.FINISHED);
		}
		return this;
	}
	
	private void delSelf(){
		new DBClosure<Boolean>() {
			@Override
			protected Boolean execute(ODatabaseDocument db) {
				db.delete(getOTaskSessionPersisted().getDocument());
				return true;
			}
		}.execute();	
	}

	@Override
	public OTaskSessionRuntime interrupt() throws Exception {
		ITaskSessionCallback callback = getCallback();
		if(callback==null) throw new IllegalStateException("Session can't be interrupted: no callback specified");
		callback.interrupt();
		getOTaskSessionPersisted().getDocument().field(Field.FINISH_TIMESTAMP.fieldName(), new Date());
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
		getOTaskSessionPersisted().persist(Field.STATUS.fieldName(), status.name());
	}

	@Override
	public OTaskSessionRuntime getOTaskSessionRuntime() {
		return this;
	}

	@Override
	public OTaskSessionPersisted getOTaskSessionPersisted() {
		return persistedSession;
	}

	@Override
	public OTaskSessionRuntime setProgress(double progress) {
		getOTaskSessionPersisted().setProgress(progress);
		return this;
	}

	@Override
	public double getProgress() {
		return getOTaskSessionPersisted().getProgress();
	}

	@Override
	public OTaskSessionRuntime setFinalProgress(double progress) {
		getOTaskSessionPersisted().setFinalProgress(progress);
		return this;
	}

	@Override
	public double getFinalProgress() {
		return getOTaskSessionPersisted().getFinalProgress();
	}

	@Override
	public OTaskSessionRuntime setCurrentProgress(double progress) {
		getOTaskSessionPersisted().setCurrentProgress(progress);
		return this;
	}

	@Override
	public double getCurrentProgress() {
		return getOTaskSessionPersisted().getCurrentProgress();
	}
	
	@Override
	public ITaskSession incrementCurrentProgress() {
		setCurrentProgress(getCurrentProgress()+1);
		return this;
	}

	public ITaskSession setOTask(OTask oTask) {
		getOTaskSessionPersisted().persist( Field.TASK_LINK.fieldName(),oTask.getDocument().getIdentity());
		return null;
	}

}

