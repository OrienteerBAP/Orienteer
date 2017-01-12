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

	public List<OTaskSessionViewer> getActiveTaskSessions(){
		return getActiveTaskSessions(OrientDbWebSession.get().getDatabase());
	}
	
	public List<OTaskSessionViewer> getActiveTaskSessions(ODatabaseDocument db){
		List<ODocument> dbResult = db.query(
				new OSQLSynchQuery<>(
						"select from "+OTaskSession.TASK_SESSION_CLASS+" where "+OTaskSession.Field.STATUS.fieldName()+"=?"
				),OTaskSession.Status.RUNNING);
		List<OTaskSessionViewer> result = new ArrayList<OTaskSessionViewer>();
		for (ODocument doc : dbResult) {
			result.add(new OTaskSessionViewer(doc));
		}
		return result;
	}
	
	public void init(ODatabaseDocument db){
		List<OTaskSessionViewer> sessions = getActiveTaskSessions(db);
		for (OTaskSessionViewer oTaskSession : sessions) {
			oTaskSession.detachUpdate();
		}
	}
}
