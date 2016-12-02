package org.orienteer.camel.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.lang.StringUtils;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OResultSet;

public class OrientDBProducer extends DefaultProducer{

	public OrientDBProducer(Endpoint endpoint) {
		super(endpoint);
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		OrientDBEndpoint endpoint = (OrientDBEndpoint)getEndpoint();
		ODatabaseDocument db = endpoint.getDatabase();
		
		Object input = exchange.getIn().getBody();
		Object sqlInput = null;
		Message out = exchange.getOut(); 
		out.getHeaders().putAll(exchange.getIn().getHeaders());

		if(input instanceof Map || input == null){
			Object dbResult = db.command(new OCommandSQL(endpoint.getSQLQuery())).execute(input);
			out.setBody(endpoint.makeOutObject(dbResult));
		}else if (input instanceof List){
			List<Object> inputList = (List)input;
			List<Object> outputList = new ArrayList<Object>();
			for (Object inputElement : inputList) {
				if (inputElement instanceof Map){
					Object dbResult = db.command(new OCommandSQL(endpoint.getSQLQuery())).execute(inputElement);
					if (dbResult instanceof List){
						outputList.addAll((List)dbResult);
					}else{
						outputList.add(dbResult);
					}
				}else{
					throw new Exception("Unknown type of input ELEMENT: "+input.getClass().toString());
				}
			}
			out.setBody(endpoint.makeOutObject(outputList));
		}else{
			throw new Exception("Unknown type of input BODY: "+input.getClass().toString());
		} 
		
		
	}


}
