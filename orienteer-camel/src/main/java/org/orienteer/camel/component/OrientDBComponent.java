package org.orienteer.camel.component;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class OrientDBComponent extends UriEndpointComponent{

	public static String DB_URL="orientdb.url"; 
	public static String DB_USERNAME="orientdb.username"; 
	public static String DB_PASSWORD="orientdb.password"; 
	
	public OrientDBComponent() {
		super(OrientDBEndpoint.class);
	}

	public OrientDBComponent(CamelContext context) {
		super(context,OrientDBEndpoint.class);
	}
	
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

		OrientDBEndpoint ep = new OrientDBEndpoint(uri,this,remaining,parameters);
		return ep;
	}

}
