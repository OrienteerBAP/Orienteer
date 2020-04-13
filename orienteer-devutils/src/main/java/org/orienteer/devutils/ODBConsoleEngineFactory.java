package org.orienteer.devutils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ru.ydn.wicket.wicketconsole.IScriptEngine;
import ru.ydn.wicket.wicketconsole.IScriptEngineFactory;

/**
 * Factory for {@link ODBConsoleEngine} 
 */
public class ODBConsoleEngineFactory implements IScriptEngineFactory {
	
	public static final String ENGINE_NAME = "OConsole";
	private static final List<String> SUPPORTED = Arrays.asList(ENGINE_NAME);

	@Override
	public IScriptEngine createScriptEngine(String name) {
		if(ENGINE_NAME.equals(name)) {
			return new ODBConsoleEngine();
		} else return null;
	}

	@Override
	public Collection<String> getSupportedEngines() {
		return SUPPORTED;
	}

}