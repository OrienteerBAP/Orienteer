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
		OrientDBEndpoint endpoint = (OrientDBEndpoint)getEndpoint();
		ODatabaseDocument db = endpoint.getDatabase();
		Object dbResult = db.command(new OCommandSQL(endpoint.getSQLQuery())).execute();
		
		Exchange exchange = getEndpoint().createExchange();
		exchange.getIn().setBody(endpoint.makeOutObject(dbResult));
		getProcessor().process(exchange);
		
		super.doStart();
	}
}
