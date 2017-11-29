package org.orienteer.core.tasks;

import com.google.common.collect.MapMaker;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import org.apache.wicket.MetaDataKey;
import org.orienteer.core.OrienteerWebApplication;

import java.util.Collection;
import java.util.Map;

/**
 * Task Manager class to provide required management capabilities over set of running tasks 
 */
public class OTaskManager {
	
	private static final MetaDataKey<OTaskManager> TASK_MANAGER_KEY = new MetaDataKey<OTaskManager>(){};
	
	private Map<ORID, OTaskSessionRuntime> activeSessions = new MapMaker().weakValues().makeMap(); 

	private OTaskManager() {
	}
	
	public static OTaskManager get() {
		OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
		OTaskManager taskManager = app.getMetaData(TASK_MANAGER_KEY);
		if(taskManager==null) {
			synchronized(OTaskManager.class){
				taskManager = app.getMetaData(TASK_MANAGER_KEY);
				if(taskManager==null){
					taskManager = new OTaskManager();
					app.setMetaData(TASK_MANAGER_KEY, taskManager);
				}
			}
		}
		return taskManager;
	}
	
	public Collection<OTaskSessionRuntime> getActiveTaskSessions() {
		return activeSessions.values();
	}
	
	public boolean isActive(OIdentifiable doc) {
		return activeSessions.containsKey(doc.getIdentity());
	}
	
	public boolean isActive(OTaskSession holder) {
		return isActive(holder.getDocument());
	}
	
	public OTaskSessionRuntime getTaskSession(OTaskSession holder) {
		return activeSessions.get(holder.getDocument().getIdentity());
	}
	
	void register(OTaskSessionRuntime session) {
		activeSessions.put(session.getOTaskSessionPersisted().getDocument().getIdentity(), session);
	}
	
	void unregister(OTaskSessionRuntime session) {
		activeSessions.remove(session.getOTaskSessionPersisted().getDocument().getIdentity());
	}

}
