package org.orienteer.bpm.camunda.scripting;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.impl.scripting.engine.Resolver;

import com.orientechnologies.orient.core.command.script.OScriptDocumentDatabaseWrapper;
import com.orientechnologies.orient.core.command.script.OScriptOrientWrapper;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.function.OFunctionUtilWrapper;

/**
 * Resolver to bind OrientDB objects 
 */
public class OResolver implements Resolver{
	
	private Map<String, Object> variables = new HashMap<>();
	
	public OResolver(ODatabaseDocumentTx db) {
		variables.put("db", new OScriptDocumentDatabaseWrapper(db));
		variables.put("orient", new OScriptOrientWrapper(db));
		variables.put("util", new OFunctionUtilWrapper());
	}

	@Override
	public boolean containsKey(Object key) {
		return variables.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		return variables.get(key);
	}

	@Override
	public Set<String> keySet() {
		return variables.keySet();
	}

}
