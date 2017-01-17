package org.orienteer.core.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.MetaDataKey;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * 
 *
 */
public class OTaskManager {

	public OTaskManager() {
		// TODO Auto-generated constructor stub
	}

	public List<OTaskSessionODocumentWrapper> getActiveTaskSessions(){
		return getActiveTaskSessions(OrientDbWebSession.get().getDatabase());
	}
	
	public List<OTaskSessionODocumentWrapper> getActiveTaskSessions(ODatabaseDocument db){
		List<ODocument> dbResult = db.query(
				new OSQLSynchQuery<>(
						"select from "+OTaskSession.TASK_SESSION_CLASS+" where "+OTaskSession.Field.STATUS.fieldName()+"=?"
				),OTaskSession.Status.RUNNING);
		List<OTaskSessionODocumentWrapper> result = new ArrayList<OTaskSessionODocumentWrapper>();
		for (ODocument doc : dbResult) {
			result.add(new OTaskSessionODocumentWrapper(doc));
		}
		return result;
	}
	
	public void init(ODatabaseDocument db){
		List<OTaskSessionODocumentWrapper> sessions = getActiveTaskSessions(db);
		for (OTaskSessionODocumentWrapper oTaskSession : sessions) {
			oTaskSession.detachUpdate();
		}
	}
}
