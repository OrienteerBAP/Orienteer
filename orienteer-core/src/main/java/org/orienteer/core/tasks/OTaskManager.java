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

public class OTaskManager {

	public static final Map<String,Class <? extends IRealTask>> TASK_TYPES = new HashMap<String,Class <? extends IRealTask>>();
	static
	{
		TASK_TYPES.put("console", OConsoleTask.class);
		//TASK_DATA_LIST.add("name");
	}	


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
	
	public OTask startNewTask(ODocument taskDoc) throws Exception{
		Object type = taskDoc.field(OTask.TYPE_FIELD);
		if (TASK_TYPES.containsKey(type)){
			return new OTask(taskDoc);
		}else{
			throw new Exception("Unknown type of task : "+type);
		}
	}
}
