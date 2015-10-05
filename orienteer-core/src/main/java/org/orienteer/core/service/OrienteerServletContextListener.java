package org.orienteer.core.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * {@link GuiceServletContextListener} to provide either predefinedInjector or create a new one by {@link OrienteerInitModule}
 */
public class OrienteerServletContextListener extends
		GuiceServletContextListener {
	
	public static Injector predefinedInjector;

	@Override
	protected Injector getInjector() {
		return predefinedInjector!=null?predefinedInjector:Guice.createInjector(new OrienteerInitModule());
	}

}
