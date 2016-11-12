package org.orienteer.devutils;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;

import ru.ydn.wicket.wicketconsole.ScriptExecutor;
import ru.ydn.wicket.wicketconsole.ScriptResultRendererManager;

/**
 * {@link IInitializer} for 'orienteer-devutils' module
 */
public class Initializer implements IInitializer
{
	@Override
	public void init(Application application) {
		ScriptExecutor.registerScriptEngineFactory(new ODBScriptEngineFactory());
		ScriptResultRendererManager.INSTANCE.registerRenderer(new ODBScriptResultRenderer());
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(Module.class);
		
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}
	
}
