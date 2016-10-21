package org.orienteer.incident.logger.driver;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.incident.logger.driver.component.OrienteerIncidentConfigurator;

import ru.asm.utils.incident.logger.IncidentLogger;
import ru.asm.utils.incident.logger.core.DefaultConfigurator;
import ru.asm.utils.incident.logger.core.ILogger;

/**
 * {@link IInitializer} for 'incident.logger.driver' module
 */
public class Initializer implements IInitializer
{
	@Override
	public void init(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(IncidentLoggerModule.class);
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}
	
}
