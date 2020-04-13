package org.orienteer.core.dao;

import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Guice {@link Provider} for {@link IODocumentWrapper}
 */
public class ODocumentWrapperProvider extends AbstractDynamicProvider{

	private static final Logger LOG = LoggerFactory.getLogger(ODocumentWrapperProvider.class);
	@Inject
	public ODocumentWrapperProvider(Injector injector) {
		super(injector);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object get(Class<?> clazz) {
		return DAO.create(clazz);
	}
}