package org.orienteer.core.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.inject.Provider;

/**
 * Guice {@link Provider} for {@link IODocumentWrapper}
 */
public class ODocumentWrapperProvider extends AbstractDynamicProvider {

	@Inject
	public ODocumentWrapperProvider(Injector injector) {
		super(injector);
	}
	
	@Override
	protected Object get(Class<?> clazz) {
		return DAO.create(clazz);
	}
}