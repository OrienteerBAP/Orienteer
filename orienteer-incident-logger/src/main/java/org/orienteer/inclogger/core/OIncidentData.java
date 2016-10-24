package org.orienteer.inclogger.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.inclogger.client.OIncident;
import org.orienteer.inclogger.core.interfaces.IData;
import org.orienteer.inclogger.core.interfaces.ILoggerData;

import com.google.gson.Gson;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * 
 */
public class OIncidentData implements IData{

	private Gson gson = new Gson();

	public OIncidentData() {
	}
	
	public void applyLoggerData(final ILoggerData<?> loggerData) {
		List<OIncident> incidents = (List<OIncident>) loggerData.get(); 
		
		for (OIncident incident : incidents){
			ODocument doc = new ODocument("OIncident");
			for(Entry<String, String> entry : incident.entrySet()) {
				doc.field(entry.getKey(),entry.getValue());
			}			
			doc.field("sended",0);
			doc.save();
		}
	}

	@Override
	public String getData() {
		return getData(IDataFlag.NOTHING);
	}

	@Override
	public String getData(IDataFlag flag) {
		ODatabaseDocument db = OrienteerWebApplication.get().getDatabase();//IncidentLoggerModule.db;//new ODatabaseDocumentTx(settings.getDBUrl());
		List<OIncident> data = new ArrayList<OIncident>();
		if (db.isActiveOnCurrentThread()){
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from OIncident where sended < ?");
			List<ODocument> queryData = db.command(query).execute(2);
			for (ODocument incidentDoc : queryData){
				data.add(new OIncident(incidentDoc));
				if (flag == IDataFlag.SENDED){
					incidentDoc.field("sended",1);
					incidentDoc.save();
				}
			}
		}
		return gson.toJson(data);
	}

	@Override
	public void mark(IDataFlag before, IDataFlag now) {
		if (before ==IDataFlag.SENDED && now == IDataFlag.SENDED_SUCCESSFULLY){
			ODatabaseDocument db = OrienteerWebApplication.get().getDatabase();
			if (db.isActiveOnCurrentThread()){
				db.command(new OCommandSQL("update OIncident set sended=2 where sended=1")).execute();
			}
		}
	}

	@Override
	public void applyData(String newData) {
		List<Map<String,String>> anotherData = gson.fromJson(newData, ArrayList.class);
		for (Map<String,String> incident : anotherData){
			ODocument doc = new ODocument("OIncident");
			for(Entry<String, String> entry : incident.entrySet()) {
				doc.field(entry.getKey(),entry.getValue());
			}		
			doc.field("sended",2);
			doc.field("recieved",1);
			doc.save();
		}
	}
}
