package org.orienteer.bpm.camunda.scripting;

import com.orientechnologies.orient.core.command.script.OScriptDocumentDatabaseWrapper;
import com.orientechnologies.orient.core.command.script.OScriptOrientWrapper;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.function.OFunctionUtilWrapper;
import org.camunda.bpm.engine.impl.scripting.engine.Resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolver to bind OrientDB objects 
 */
public class OResolver implements Resolver{
	
	private Map<String, Object> variables = new HashMap<>();
	
	public OResolver(ODatabaseSession db) {
		variables.put("db", new OScriptDocumentDatabaseWrapper((ODatabaseDocumentInternal) db));
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
