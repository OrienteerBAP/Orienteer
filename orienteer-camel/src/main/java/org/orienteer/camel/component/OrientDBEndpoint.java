package org.orienteer.camel.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.commons.lang.StringUtils;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

@UriEndpoint(scheme = "orientdb", syntax = "orientdb:sqlQuery", title = "OrientDB") 
public class OrientDBEndpoint extends DefaultEndpoint {

	private String remaining;
	private Map<String, Object> parameters;

	@UriParam
	boolean asJson;

	protected OrientDBEndpoint(String endpointUri,Component component,String remaining, Map<String, Object> parameters ) {
		super(endpointUri,component);
		this.remaining = remaining;
		this.parameters = parameters;
    }

	protected OrientDBEndpoint(String endpointUri, Component component) {
		super(endpointUri,component);
    }
	
	@Override
	public Producer createProducer() throws Exception {

		return new OrientDBProducer(this);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {

		return new OrientDBConsumer(this, processor);
	}

	@Override
	public boolean isSingleton() {

		return false;
	}
	
	public String getSQLQuery(){
		return remaining;
	}
	
	public Map<String, Object> getParameters(){
		return parameters;
	}

	public boolean isAsJson() {
		return asJson;
	}

	/* every object saved to json, but List contains all that objects */
	public void setAsJson(boolean asJson) {
		this.asJson = asJson;
	} 
	
	public ODatabaseDocument getDatabase(){
		
		String url = getCamelContext().getProperty(OrientDBComponent.DB_URL);
		String username = getCamelContext().getProperty(OrientDBComponent.DB_USERNAME);
		String password = getCamelContext().getProperty(OrientDBComponent.DB_PASSWORD);
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(url).open(username, password);
		return db;
	}
	
	public Object makeOutObject(Object rawOut) throws Exception{
		Object result;
		if (rawOut instanceof OResultSet){
			List<Object> resultArray = new ArrayList<Object>();
			OResultSet tmpset = (OResultSet) rawOut;
			for (Object object : tmpset) {
				if (object instanceof ODocument){
					ODocument doc = ((ODocument)object);
					if (isAsJson()){
						resultArray.add(doc.toJSON());
					}else{
						Map<String,String> fieldsMap = new HashMap<String,String>();
						for (String fieldName : doc.fieldNames()) {
							Object field = doc.field(fieldName);
							if (field instanceof OIdentifiable){
								fieldsMap.put(fieldName, ((OIdentifiable)field).getIdentity().toString());
							}else if(field != null) {
								fieldsMap.put(fieldName, field.toString());
							}else{
								fieldsMap.put(fieldName, null);
							}
						}
						resultArray.add(fieldsMap);
					}
				}else if(object instanceof OIdentifiable){
					resultArray.add(((OIdentifiable)object).getIdentity().toString());
				}else{
					throw new Exception("Unknown type of OrientDB object:"+object.getClass());
				}
			}

			result = resultArray;
		}else{
			result = rawOut;
		}
		return result;
	}
}
