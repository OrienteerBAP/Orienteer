package org.orienteer.camel.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class OrientDBConsumer extends DefaultConsumer{

	public OrientDBConsumer(Endpoint endpoint, Processor processor) {
		super(endpoint, processor);
		
	}

	@Override
	protected void doStart() throws Exception {
		Exchange exchange = getEndpoint().createExchange();
		exchange.getIn().setBody("orientDB data set and other stuff");
		getProcessor().process(exchange);
		super.doStart();
	}
}
