package org.orienteer.core.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.common.concur.ONeedRetryException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Multithread updater for {@link OTaskSession}
 *
 */
public class OTaskSessionUpdater {
	
	private static final Logger LOG = LoggerFactory.getLogger(OTaskSessionUpdater.class);

	
	private static final int QUEUE_LIMIT=1000;
	private static final int WRITE_DELAY_MAX=1000;
	private static final int RETRIES_LIMIT=10;
	private static final int RETRIES_SLEEP_MAX=300;
	
	/**
	 * Supported commands
	 */
	private enum Commands{
		SET,APPEND,INCREMENT
	}
	/**
	 * Inner data
	 * @param <T> type of data
	 */
	private class UpdaterData<T>{
		public Commands command;
		public String field;
		public T data;
		public UpdaterData(Commands command,String field,T data) {
			this.command=command;
			this.field=field;
			this.data=data;
		}
	}
	
	private IOrientDbSettings dbSettings;
	private volatile ODocument taskSessionDoc;
	
	private AtomicBoolean running;
	private AtomicBoolean saving;
	private AtomicBoolean deleting;
	
	private Thread innerThread;
	
	private BlockingQueue<UpdaterData<?>> queue;

	public OTaskSessionUpdater(ODocument taskSessionDoc,IOrientDbSettings dbSettings) {
		running = new AtomicBoolean(false);
		saving = new AtomicBoolean(false);
		deleting = new AtomicBoolean(false);
		queue = new ArrayBlockingQueue<UpdaterData<?>>(QUEUE_LIMIT);
		this.dbSettings=dbSettings;
		this.taskSessionDoc=taskSessionDoc;
	}
	//////////////////////////////////////////////////////////////////////
	public void set(String field,Object data){
		queue.add(new UpdaterData<Object>(Commands.SET,field,data));
	} 
	
	public void append(String field,String data){
		queue.add(new UpdaterData<String>(Commands.APPEND,field,data));
	}
	
	public void increment(String field,long data){
		queue.add(new UpdaterData<Long>(Commands.INCREMENT,field,data));
	}
	
	/**
	 * delete current session from DB
	 */
	public void deleteSession(){
		queue.clear();
		deleting.getAndSet(true);
	}

	public void doSave(){
		saving.getAndSet(true);
	}
	
	
	//////////////////////////////////////////////////////////////////////
	
	private boolean isRunning(){
		return running.get() || (!queue.isEmpty() && saving.get());
	}
	
	private boolean isNeedToSave(){
		return saving.get() && !queue.isEmpty();
	}

	private boolean isNeedToDelete(){
		return deleting.get();
	}
	
	private ODatabaseDocumentTx dbOpen() {
		@SuppressWarnings("resource")
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(dbSettings.getDBUrl()).open(dbSettings.getDBInstallatorUserName(), dbSettings.getDBInstallatorUserPassword());
		return db;
	}
	
	private void dbClose(ODatabaseDocumentTx db) {
		db.close();
	}
	
	
	public void start(){
		running.getAndSet(true);
		innerThread = new Thread(new Runnable(){
			@Override
			public void run() {
				mainLoop();
			}
		});
		innerThread.start();
	}
	
	public void stop(){
		running.getAndSet(false);
	}
	
	private void mainLoop(){
		while(isRunning()){
			try {
				if(isNeedToDelete()){
					ODatabaseDocumentTx db = dbOpen();
					taskSessionDoc.delete();
					dbClose(db);
					return;
				}else
				if (isNeedToSave()){
					ODatabaseDocumentTx db = dbOpen();
					while(!queue.isEmpty()){
						int nowToSave = queue.size();  
						ArrayList<UpdaterData<?>> listToSave = new ArrayList<UpdaterData<?>>(nowToSave);
						for (int i = 0; i < nowToSave; ++i) {
							UpdaterData<?> curData = queue.take();
							listToSave.add(curData);
						}
						int retry = 0;
						for (; retry < RETRIES_LIMIT; ++retry) {
							try{
								loadUpdatesFromList(listToSave);
								taskSessionDoc.save();
								break;
							}catch (ONeedRetryException e ) {
								LOG.error(e.getMessage());
								Thread.sleep(Math.round(Math.random()*RETRIES_SLEEP_MAX));
								taskSessionDoc.reload();
							}
						}
						if (retry==RETRIES_LIMIT){ 
							LOG.error("Unable to write DB record "+taskSessionDoc.getIdentity()+" !");
							return;
						}
					}
					dbClose(db);
					saving.getAndSet(false);
				}
				Thread.sleep(WRITE_DELAY_MAX);
			} catch (InterruptedException e) {
				return;
			}
		}		
	}
	
	private void loadUpdatesFromList(List<UpdaterData<?>> list){
		for (UpdaterData<?> curData : list) {
			switch (curData.command) {
			case SET:
				taskSessionDoc.field(curData.field,curData.data);
				break;
			case APPEND:
				String oldString = (String)taskSessionDoc.field(curData.field);
				if (oldString==null){
					taskSessionDoc.field(curData.field,curData.data);
				}else{
					taskSessionDoc.field(curData.field,Strings.join("", oldString,(String)curData.data));
				}
				break;
			case INCREMENT:
				Long oldLong = (Long)taskSessionDoc.field(curData.field);
				if (oldLong==null){
					taskSessionDoc.field(curData.field,curData.data);
				}else{
					taskSessionDoc.field(curData.field,oldLong+(Long)curData.data);
				}
				break;
			default:
				break;
			}
		}
	}

	
}
