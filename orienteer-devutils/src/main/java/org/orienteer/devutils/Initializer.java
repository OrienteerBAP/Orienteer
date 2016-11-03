package org.orienteer.devutils;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.devutils.component.ODBScriptEngineInterlayer;
import org.orienteer.devutils.component.ODBScriptEngineInterlayerResult;
import org.orienteer.devutils.component.ODBScriptEngineInterlayerResultRenderer;

import ru.ydn.wicket.wicketconsole.ScriptEngineInterlayerManager;
import ru.ydn.wicket.wicketconsole.ScriptEngineInterlayerRendererManager;

/**
 * {@link IInitializer} for 'orienteer-devutils' module
 */
public class Initializer implements IInitializer
{
	@Override
	public void init(Application application) {
		ScriptEngineInterlayerManager.INSTANCE.addInterlayer("SQL", ODBScriptEngineInterlayer.class);
		ScriptEngineInterlayerRendererManager.INSTANCE.addRenderer(ODBScriptEngineInterlayerResultRenderer.class,ODBScriptEngineInterlayerResult.class);
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(Module.class);
		
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}
	
}
