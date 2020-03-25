package org.orienteer.core.dao;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Guice {@link Provider} for generic DAO objects
 */
public class DAOProvider extends AbstractDynamicProvider{

	private static final Logger LOG = LoggerFactory.getLogger(DAOProvider.class);
	@Inject
	public DAOProvider(Injector injector) {
		super(injector);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object get(Class<?> clazz) {
		return DAO.dao(clazz);
	}
}