package org.orienteer.devutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ru.ydn.wicket.wicketconsole.IScriptEngine;
import ru.ydn.wicket.wicketconsole.IScriptEngineFactory;

/**
 * {@link IScriptEngineFactory} for creating {@link ODBScriptEngine} for SQL execution in OrientDB 
 */
public class ODBScriptEngineFactory implements IScriptEngineFactory {
	
	public static final String ENGINE_NAME = "SQL";
	private static final List<String> SUPPORTED = Arrays.asList(ENGINE_NAME);

	@Override
	public IScriptEngine createScriptEngine(String name) {
		if(ENGINE_NAME.equals(name)) {
			return new ODBScriptEngine();
		} else return null;
	}

	@Override
	public Collection<String> getSupportedEngines() {
		return SUPPORTED;
	}

}
