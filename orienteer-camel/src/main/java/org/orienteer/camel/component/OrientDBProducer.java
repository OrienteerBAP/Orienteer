package org.orienteer.camel.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.apache.wicket.util.string.Strings;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentHelper;
import com.orientechnologies.orient.core.sql.OCommandSQL;

public class OrientDBProducer extends DefaultProducer{

	public OrientDBProducer(Endpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		OrientDBEndpoint endpoint = (OrientDBEndpoint)getEndpoint();
		ODatabaseDocument db = endpoint.databaseOpen();
		Object input = exchange.getIn().getBody();
		Message out = exchange.getOut(); 
		out.getHeaders().putAll(exchange.getIn().getHeaders());

		
		if (input instanceof List){
			out.setBody(endpoint.makeOutObject(processList((List<?>)input, endpoint, db)));
		}else if (input instanceof String && isJSONList((String)input)){
			List<String> inputList =  strToJSONsList((String)input);
			out.setBody(endpoint.makeOutObject(processList(inputList, endpoint, db)));
		}else{
			out.setBody(endpoint.makeOutObject(processSingleObject(input, endpoint, db)));
		}
		endpoint.databaseClose(db);
	}
	
	private boolean isJSONList(String input){
		return false;
		//return input.matches("^[{.*");
	}
	
	private boolean isJsonObject(String input){
		return input.matches("^\\{.*\\}$");
	}
	
	private List<String> strToJSONsList(String str){
		str = str.substring(1, str.length() - 1);
		// TODO: replace this ugly method  
		return Arrays.asList(str.split(","));		
	}
	
	private List<Object> processList(List<?> inputList,OrientDBEndpoint endpoint,ODatabaseDocument db) throws Exception{
		List<Object> outputList = new ArrayList<Object>();
		for (Object inputElement : inputList) {
			Object dbResult = processSingleObject(inputElement,endpoint,db);
			if (dbResult instanceof List){
				outputList.addAll((List<?>)dbResult);
			}else{
				outputList.add(dbResult);
			}
		}
		return outputList;
	}
	
	private Object processSingleObject(Object input,OrientDBEndpoint endpoint,ODatabaseDocument db) throws Exception{
		ODocument inputDocument = null;
		if (input instanceof Map){
			inputDocument = (ODocument) fromMap(input);
		}else if(input instanceof ODocument){
			inputDocument = fromObject((ODocument)input, endpoint, db);
		}else if(input instanceof String && isJsonObject((String)input)){
			inputDocument = fromJSON((String)input, endpoint, db);
		}
		if (inputDocument!=null){
			if (endpoint.isMakeNew()){
				inputDocument.getIdentity().reset();
			}
			if(endpoint.isPreload()){
				inputDocument.save();
			}
			if (!Strings.isEmpty(endpoint.getSQLQuery())){
				Object dbResult = db.command(new OCommandSQL(endpoint.getSQLQuery())).execute(inputDocument.toMap());
				return dbResult;
			}
			return inputDocument;
		}else{
			if (!Strings.isEmpty(endpoint.getSQLQuery())){
				if (input instanceof List){
					convertLinks((List<Object>)input);
					Object dbResult = db.command(new OCommandSQL(endpoint.getSQLQuery())).execute(((List<?>)input).toArray());
					return dbResult;
				}else{
					Object dbResult = db.command(new OCommandSQL(endpoint.getSQLQuery())).execute(input);
					return dbResult;
				}
			}
		}
		return input;
	}
	
	private void convertLinks(List<Object> input){
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).toString().matches("^.+:.+$")){
				input.set(i, new ORecordId(input.get(i).toString()));
			}
		}
	}
	
	private Object fromMap(Object input){
		if (input instanceof Map){//something like ODocument
			Map<?, ?> objMap = (Map<?,?>)input;
			String rid = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_RID));
			String clazz = (String)(objMap.remove(ODocumentHelper.ATTRIBUTE_CLASS));
			if (rid!=null || clazz!=null){
				ODocument result=null;
				if (rid!=null && clazz!=null && objMap.isEmpty()){ //it is document link
					result = new ODocument(clazz,new ORecordId(rid));
				}else if (clazz!=null && (rid==null || ((OrientDBEndpoint)getEndpoint()).isMakeNew() )){//it is embedded or new document  
					result = new ODocument(clazz);
				}else if (rid!=null && clazz!=null){ //it is document
					result = new ODocument(clazz,new ORecordId(rid));
				}else if (rid!=null){//it is something like broken link  
					result = new ODocument(new ORecordId(rid));
				}
				for (Entry<?, ?> entry : objMap.entrySet()) {
					result.field((String) entry.getKey(),fromMap(entry.getValue()));
				}
				return result;
			}else{//wow,it is just Map
				Map<String,Object> result = new HashMap<String,Object>();
				for (Entry<?, ?> entry : objMap.entrySet()) {
					result.put((String) entry.getKey(),fromMap(entry.getValue()));
				}
				return result;
			}
		}else if (input instanceof Iterable){//something like list
			ArrayList<Object> result = new ArrayList<Object>(); 
			for (Object item : ((Iterable<?>)input)) {
				result.add(fromMap(item));
			}
			return result;
		}
		return input;		
	}

	private ODocument fromJSON(String input,OrientDBEndpoint endpoint,ODatabaseDocument db){
		ODocument out = new ODocument();
		out.fromJSON(input);
		return out;
	}

	private ODocument fromObject(ODocument input,OrientDBEndpoint endpoint,ODatabaseDocument db){
		return input;
	}
	

}
