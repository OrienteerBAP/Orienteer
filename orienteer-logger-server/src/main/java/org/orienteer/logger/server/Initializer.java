package org.orienteer.logger.server;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;

/**
 * {@link IInitializer} for 'orienteer-logger-server' module
 */
public class Initializer implements IInitializer
{
	@Override
	public void init(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(OLoggerModule.class);
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}
	
}
