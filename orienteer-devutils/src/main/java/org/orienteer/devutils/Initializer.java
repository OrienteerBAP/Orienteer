package org.orienteer.devutils;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.devutils.component.ODBScriptEngineInterlayer;

import ru.ydn.wicket.wicketconsole.ScriptEngineInterlayerManager;

/**
 * {@link IInitializer} for 'orienteer-devutils' module
 */
public class Initializer implements IInitializer
{
	@Override
	public void init(Application application) {
		ScriptEngineInterlayerManager.INSTANCE.addInterlayer("SQL", ODBScriptEngineInterlayer.class);
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(Module.class);
		
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}
	
}
