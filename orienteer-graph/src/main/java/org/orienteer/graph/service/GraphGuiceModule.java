package org.orienteer.graph.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Guice module to provide graphs related stuff
 */
public class GraphGuiceModule extends AbstractModule {

	@Override
	protected void configure() {

	}
	
	@Provides
	@RequestScoped
	public OrientGraph provideOrientGraph(ODatabaseDocumentTx dbTx)
	{
		return new OrientGraph(dbTx, false);
	}

}
