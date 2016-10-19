package org.orienteer.incident.logger.driver.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.incident.logger.driver.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.asm.utils.incident.logger.core.IData;
import ru.asm.utils.incident.logger.core.ILoggerData;
import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

/**
 * 
 */
public class OrienteerIncidentData implements IData{

	//private Map<Integer,OrienteerIncident> data;
	private Integer counter = 0;
	private Gson gson = new Gson();

	public OrienteerIncidentData() {
		//data = new HashMap<Integer,OrienteerIncident>();
	}
	
	public void applyLoggerData(final ILoggerData<?> loggerData) {
		
		//ODatabaseDocumentTx db = new ODatabaseDocumentTx("remote:localhost/Orienteer").open("admin", "admin");


/*
		ODocument doc = new ODocument("Person");
		doc.
		doc.field( "id", "Luke" );
		doc.field( "surname", "Skywalker" );
		doc.field( "city", new ODocument("City").field("name","Rome").field("country", "Italy") );

		// SAVE THE DOCUMENT
		doc.save();

		db.close();
		
		
		*/
		List<OrienteerIncident> incidents = (List<OrienteerIncident>) loggerData.get(); 
		
		for (OrienteerIncident incident : incidents){
			ODocument doc = new ODocument("OIncident");
			for(Entry<String, String> entry : incident.entrySet()) {
				doc.field(entry.getKey(),entry.getValue());
			}			
			doc.field("sended",0);
			doc.save();
		}
		
		
//		this.data.put(counter++, (List<OrienteerIncident>) loggerData.get());
	}

	@Override
	public String getData() {
		return getData(IDataFlag.NOTHING);
	}

	@Override
	public String getData(IDataFlag flag) {
		//List<OrienteerIncident> data = new ArrayList<OrienteerIncident>();
		ODatabaseDocument db = Module.db;//new ODatabaseDocumentTx(settings.getDBUrl());
		List<OrienteerIncident> data = new ArrayList<OrienteerIncident>();
		if (db!=null){
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("select from OIncident where sended < ?");
			List<ODocument> queryData = db.command(query).execute(2);
			for (ODocument incidentDoc : queryData){
				data.add(new OrienteerIncident(incidentDoc));
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
	        System.out.println( " MARK DOWN!!!! " );
			ODatabaseDocument db = Module.db;//new ODatabaseDocumentTx(settings.getDBUrl());
			if (db!=null){
				//db.commit();
				db.command(new OCommandSQL("update OIncident set sended=2 where sended=1")).execute();
//				db.command(new OSQLSynchQuery<Object>("update OIncident set sended=2 where sended=1"));
				//db.commit();
		        System.out.println( " REALLY DOWN!!!! " );
				
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
