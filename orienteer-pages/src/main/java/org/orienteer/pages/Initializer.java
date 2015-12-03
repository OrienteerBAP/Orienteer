package org.orienteer.pages;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.pages.module.PagesModule;

/**
 * Wicket {@link IInitializer} for Orienteer Graph module
 */
public class Initializer implements IInitializer {

	@Override
	public void init(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		app.registerModule(PagesModule.class);
	}

	@Override
	public void destroy(Application application) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
	}

}
