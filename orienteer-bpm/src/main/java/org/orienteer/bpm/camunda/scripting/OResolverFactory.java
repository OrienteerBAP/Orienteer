package org.orienteer.bpm.camunda.scripting;

import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.PersistenceSession;
import org.camunda.bpm.engine.impl.scripting.engine.Resolver;
import org.camunda.bpm.engine.impl.scripting.engine.ResolverFactory;
import org.orienteer.bpm.camunda.OPersistenceSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * {@link ResolverFactory} to object {@link Resolver} for binding OrientDB objects to camunda env. 
 */
public class OResolverFactory implements ResolverFactory{

	@Override
	public Resolver createResolver(VariableScope variableScope) {
		OPersistenceSession session = (OPersistenceSession) Context.getCommandContext().getSession(PersistenceSession.class);
		return new OResolver(session.getDatabase());
	}

}
