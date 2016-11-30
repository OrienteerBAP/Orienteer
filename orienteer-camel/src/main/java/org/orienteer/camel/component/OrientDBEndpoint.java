package org.orienteer.camel.component;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class OrientDBEndpoint extends DefaultEndpoint {

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
}
