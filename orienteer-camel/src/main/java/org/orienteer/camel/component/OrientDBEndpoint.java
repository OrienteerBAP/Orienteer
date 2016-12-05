package org.orienteer.camel.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentHelper;
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
						//Map<String,Object> fieldsMap = ODocumentToMap(doc,true);
						resultArray.add(marshalling(doc,true));
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
	
	private Object marshalling(Object obj,boolean withFields){
		if (obj instanceof ODocument){
			ODocument objDoc =(ODocument)obj; 
			Map<String,Object> result = new HashMap<String,Object>();
			if(withFields || objDoc.isEmbedded()){
			    for (String fieldName : objDoc.fieldNames()){
			    	result.put(fieldName, marshalling(objDoc.field(fieldName),false));
			    }
			}
			
		    final ORID id = objDoc.getIdentity();
		    if (id.isValid())
		    	result.put(ODocumentHelper.ATTRIBUTE_RID, id.toString());

			final String className = objDoc.getClassName();
			if (className != null)
				result.put(ODocumentHelper.ATTRIBUTE_CLASS, className);
			
			result.put(ODocumentHelper.ATTRIBUTE_TYPE, (char)objDoc.RECORD_TYPE);
			result.put(ODocumentHelper.ATTRIBUTE_VERSION, objDoc.getVersion());
			return result;
		}else if(obj instanceof Map){
    		Map<String,Object> result = new HashMap<String,Object>();
    		for (Entry<String, Object> entry : ((Map<String,Object>)obj).entrySet()) {
   				result.put(entry.getKey(),marshalling(entry.getValue(),false));	
			}
    		return result;
		}else if(obj instanceof Iterable){
    		List<Object> result = new ArrayList<Object>();
    		for (Object subfield : (Iterable)obj) {
    			result.add(marshalling(subfield,false));	
			}
    		return result;
		}else{
			return obj;
		}
	}
	protected Object unmarshalling(Object obj){
		if (obj instanceof Map){//something like ODocument
			Map<String,Object> objMap = (Map)obj;
			String rid = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_RID));
			String clazz = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_CLASS));
			String type = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_TYPE));
			double version = (double)(objMap.remove(ODocumentHelper.ATTRIBUTE_VERSION));
			if (rid!=null && clazz!=null && objMap.isEmpty()){//it is link
				return new ODocument(clazz, new ORecordId(rid));
			}else if(clazz!=null && rid==null ){//it is embedded
				ODocument result = new ODocument(clazz);
				for (Entry<String, Object> entry : objMap.entrySet()) {
					result.field(entry.getKey(),unmarshalling(entry.getValue()));
				}
				return result;
			}else{//wow,it is just Map
				Map<String,Object> result = new HashMap<String,Object>();
				for (Entry<String, Object> entry : objMap.entrySet()) {
					result.put(entry.getKey(),unmarshalling(entry.getValue()));
				}
				return result;
			}
		}else if (obj instanceof Iterable){//something like list
			ArrayList<Object> result = new ArrayList<Object>(); 
			for (Object item : ((Iterable)obj)) {
				result.add(unmarshalling(item));
			}
			return result;
		}
		return obj;
	}
	/*
	protected Object unmarshalling(Object obj){
		if (obj instanceof Map){//something like ODocument
			Map<String,Object> objMap = (Map)obj;
			String rid = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_RID));
			String clazz = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_CLASS));
			if (rid!=null || clazz!=null){
				ODocument result=null;
				if (rid!=null && clazz!=null){ //it is document link or document
					result = new ODocument(clazz,new ORecordId(rid));
				}else if (clazz!=null){//it is embedded document  
					result = new ODocument(clazz);
				}else if (rid!=null){//it is something like broken link  
					result = new ODocument(new ORecordId(rid));
				}
				for (Entry<String, Object> entry : objMap.entrySet()) {
					result.field(entry.getKey(),unmarshalling(entry.getValue()));
				}
				return result;
			}else{//wow,it is just Map
				Map<String,Object> result = new HashMap<String,Object>();
				for (Entry<String, Object> entry : objMap.entrySet()) {
					result.put(entry.getKey(),unmarshalling(entry.getValue()));
				}
				return result;
			}
		}else if (obj instanceof Iterable){//something like list
			ArrayList<Object> result = new ArrayList<Object>(); 
			for (Object item : ((Iterable)obj)) {
				result.add(unmarshalling(item));
			}
			return result;
		}
		return obj;
	}
	*/
	/*
	private Object marshalling(Object obj,boolean withFields){
		if (obj instanceof ODocument){
			ODocument objDoc =(ODocument)obj; 
			Map<String,Object> result = new HashMap<String,Object>();
			if(withFields || objDoc.isEmbedded()){
			    for (String fieldName : objDoc.fieldNames()){
			    	result.put(fieldName, marshalling(objDoc.field(fieldName),false));
			    }
			}
			
		    final ORID id = objDoc.getIdentity();
		    if (id.isValid())
		    	result.put(ODocumentHelper.ATTRIBUTE_RID, id);

			final String className = objDoc.getClassName();
			if (className != null)
				result.put(ODocumentHelper.ATTRIBUTE_CLASS, className);
			
			result.put(ODocumentHelper.ATTRIBUTE_TYPE, "d");
			return result;
		}else if(obj instanceof Map){
    		Map<String,Object> result = new HashMap<String,Object>();
    		for (Entry<String, Object> entry : ((Map<String,Object>)obj).entrySet()) {
   				result.put(entry.getKey(),marshalling(entry.getValue(),false));	
			}
    		return result;
		}else if(obj instanceof Iterable){
    		List<Object> result = new ArrayList<Object>();
    		for (Object subfield : (Iterable)obj) {
    			result.add(marshalling(subfield,false));	
			}
    		return result;
		}else{
			return obj;
		}
	}
	*/
	/*	//three marshall only embedded objects, without external links
	private Object marshalling(Object obj,boolean withFields){
		if (obj instanceof ODocument && (withFields || ((ODocument)obj).isEmbedded())){
			ODocument objDoc =(ODocument)obj; 
			Map<String,Object> result = new HashMap<String,Object>();
//			if(withFields || objDoc.isEmbedded()){
			    for (String fieldName : objDoc.fieldNames()){
			    	result.put(fieldName, marshalling(objDoc.field(fieldName),false));
			    }
//			}
		    final ORID id = objDoc.getIdentity();
		    if (id.isValid())
		    	result.put(ODocumentHelper.ATTRIBUTE_RID, id.toString());

			final String className = objDoc.getClassName();
			if (className != null)
				result.put(ODocumentHelper.ATTRIBUTE_CLASS, className);
			
			return result;
		}else if(obj instanceof OIdentifiable){
			return ((OIdentifiable)obj).getIdentity().toString();
		}else if(obj instanceof Map){
    		Map<String,Object> result = new HashMap<String,Object>();
    		for (Entry<String, Object> entry : ((Map<String,Object>)obj).entrySet()) {
   				result.put(entry.getKey(),marshalling(entry.getValue(),false));	
			}
    		return result;
		}else if(obj instanceof Iterable){
    		List<Object> result = new ArrayList<Object>();
    		for (Object subfield : (Iterable)obj) {
    			result.add(marshalling(subfield,false));	
			}
    		return result;
		}else{
			return obj;
		}
	}
*/
}
