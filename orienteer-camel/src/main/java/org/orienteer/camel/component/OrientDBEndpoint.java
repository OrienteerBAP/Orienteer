package org.orienteer.camel.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.wicket.util.string.Strings;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ODocumentHelper;

@UriEndpoint(scheme = "orientdb", syntax = "orientdb:sqlQuery", title = "OrientDB") 
public class OrientDBEndpoint extends DefaultEndpoint {

	private String remaining;
	private Map<String, Object> parameters;

	@UriParam(defaultValue = "map")
	private OrientDBCamelDataType outputType = OrientDBCamelDataType.map;

	@UriParam
	private String fetchPlan;
	
	@UriParam(defaultValue = "0")
	private int maxDepth = 0;

	@UriParam(defaultValue = "true")
	private boolean fetchAllEmbedded = true;
	
	@UriParam
	private String inputAsOClass;

	@UriParam
	private boolean preload;

	@UriParam
	private boolean makeNew;

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

	//should be called to open new connection
	@SuppressWarnings("resource")
	public ODatabaseDocument databaseOpen(){
		
		String url = getCamelContext().getProperty(OrientDBComponent.DB_URL);
		String username = getCamelContext().getProperty(OrientDBComponent.DB_USERNAME);
		String password = getCamelContext().getProperty(OrientDBComponent.DB_PASSWORD);
		ODatabaseDocumentTx db = new ODatabaseDocumentTx(url).open(username, password);
		return db;
	}
	
	//should be called to close existing connection
	public void databaseClose(ODatabaseDocument db){
		db.close();
	}
	
	public Object makeOutObject(Object rawOut) throws Exception{
		if (rawOut instanceof Iterable){
			List<Object> resultArray = new ArrayList<Object>();
			Iterable<?> tmpset = (Iterable<?>) rawOut;
			for (Object object : tmpset) {
				if (object instanceof ODocument){
					ODocument doc = ((ODocument)object);
					if (outputType.equals(OrientDBCamelDataType.map)){
						resultArray.add(toMap(doc));
					}else if (outputType.equals(OrientDBCamelDataType.json)){
						resultArray.add(toJSON(doc));//doc.toJSON("fetchPlan:"+getFetchPlan()));
					}else if (outputType.equals(OrientDBCamelDataType.object)){
						resultArray.add(toObject(doc));
					}else{
						throw new Exception("Unknown outputType :"+outputType.toString());
					}
				//}else if(object instanceof OIdentifiable){
				//	resultArray.add(((OIdentifiable)object).getIdentity().toString());
				}else{
					throw new Exception("Unknown type of OrientDB object:"+object.getClass());
				}
			}
			//if (outputType.equals(OrientDBCamelDataType.json)){
			//	return "["+Strings.join(",\"&!#\",", (List)resultArray)+"]";
			//}else{
				return resultArray;
			//}
		}else{
			return rawOut;
		}
	}
	
	private Object toJSON(ODocument obj){
		if (Strings.isEmpty(getFetchPlan())){
			return obj.toJSON();
		}else{
			return obj.toJSON("fetchPlan:"+getFetchPlan());
		}
	}
	
	private Object toMap(Object obj){
		return toMap(obj,0);
	}
	
	private Object toObject(Object obj){
		return obj;
	}

	
	private Object toMap(Object obj,int depth){
		if (obj instanceof ODocument){
			ODocument objDoc =(ODocument)obj; 
			Map<String,Object> result = new HashMap<String,Object>();
			if((objDoc.isEmbedded()&& isFetchAllEmbedded()) || (depth<=getMaxDepth())){
			    for (String fieldName : objDoc.fieldNames()){
			    	result.put(fieldName, toMap(objDoc.field(fieldName),depth+1));
			    }
			}
		    final ORID id = objDoc.getIdentity();
		    if (id.isValid() && id.isPersistent() )
		    	result.put(ODocumentHelper.ATTRIBUTE_RID, id.toString());

			final String className = objDoc.getClassName();
			if (className != null)
				result.put(ODocumentHelper.ATTRIBUTE_CLASS, className);
			return result;
		}else if(obj instanceof Map){
    		Map<String,Object> result = new HashMap<String,Object>();
    		Map<?, ?> source = (Map<?,?>)obj;
    		for (Entry<?, ?> entry : (source).entrySet()) {
   				result.put((String) entry.getKey(),toMap(entry.getValue(),depth+1));	
			}
    		return result;
		}else if(obj instanceof Iterable){
    		List<Object> result = new ArrayList<Object>();
    		for (Object subfield : (Iterable<?>)obj) {
    			result.add(toMap(subfield,depth+1));	
			}
    		return result;
		}else{
			return obj;
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////


	public String getFetchPlan() {
		return fetchPlan;
	}

	/**
	 * Set fetch plan (wiew orientdb documentation, like http://orientdb.com/docs/2.0/orientdb.wiki/Fetching-Strategies.html) 
	 */
	public void setFetchPlan(String fetchPlan) {
		this.fetchPlan = fetchPlan;
	}

	public String getInputAsOClass() {
		return inputAsOClass;
	}

	/**
	 * Rewrite "@class" field in root document(s) 
	 */
	public void setInputAsOClass(String inputAsOClass) {
		this.inputAsOClass = inputAsOClass;
	}

	public boolean isPreload() {
		return preload;
	}

	/**
	 * Save ODocument from input data BEFORE query   
	 */
	public void setPreload(boolean preload) {
		this.preload = preload;
	}

	public boolean isMakeNew() {
		return makeNew;
	}

	/**
	 * Clear ODocuments RID`s in PRELOAD phase BEFORE save
	 */
	public void setMakeNew(boolean makeNew) {
		this.makeNew = makeNew;
	}

	public OrientDBCamelDataType getOutputType() {
		return outputType;
	}

	/**
	 * Output data type of single row. Can be "map", "object" or "json" 
	 * Default value - "map"
	 * 
	 */
	public void setOutputType(OrientDBCamelDataType outputType) {
		this.outputType = outputType;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public boolean isFetchAllEmbedded() {
		return fetchAllEmbedded;
	}

	public void setFetchAllEmbedded(boolean fetchAllEmbedded) {
		this.fetchAllEmbedded = fetchAllEmbedded;
	}



}
