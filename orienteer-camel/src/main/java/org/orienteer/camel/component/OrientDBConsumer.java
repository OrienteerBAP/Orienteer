package org.orienteer.camel.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.orienteer.core.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketorientdb.OrientDbSettings;

public class OrientDBConsumer extends DefaultConsumer{

	public OrientDBConsumer(Endpoint endpoint, Processor processor) {
		super(endpoint, processor);
		
	}

	@Override
	protected void doStart() throws Exception {
		ODatabaseDocument db = OrienteerWebApplication.get().getDatabase();
		Object dbResult = db.command(new OCommandSQL("select from ouser")).execute();
		if (dbResult instanceof OResultSet){
			OResultSet resultset = (OResultSet) dbResult;
			for (Object object : resultset) {
				Object result;
				if (object instanceof ODocument){
					//Map<String, Object> resultMap = ((ODocument)object).toMap();
					result = ((ODocument)object).toJSON();
				}else if(object instanceof OIdentifiable){
					result = ((OIdentifiable)object).getIdentity().toString();
				}else{
					throw new Exception("Unknown type of OrientDB object:"+object.getClass());
				}
				Exchange exchange = getEndpoint().createExchange();
				Message in = exchange.getIn();
				in.setBody(result);
				getProcessor().process(exchange);
			}
			
		}else{
			Exchange exchange = getEndpoint().createExchange();
			exchange.getIn().setBody(dbResult);
			getProcessor().process(exchange);
		}
		super.doStart();
	}
}
