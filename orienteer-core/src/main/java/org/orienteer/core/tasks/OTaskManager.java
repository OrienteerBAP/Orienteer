package org.orienteer.core.tasks;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class OTaskManager {

	public OTaskManager() {
		// TODO Auto-generated constructor stub
	}
	
	public List<OTask> getActiveTasks(){
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		List<ODocument> dbResult = db.query(new OSQLSynchQuery<>("select from "+OTask.TASK_CLASS+" where "+OTask.STATUS_FIELD+"=?"),OTask.Status.RUNNING);
		List<OTask> result = new ArrayList<OTask>();
		for (ODocument doc : dbResult) {
			result.add(new OTask(doc));
		}
		return result;
	}
}
