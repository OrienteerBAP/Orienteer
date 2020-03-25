package org.orienteer.core.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.inject.Provider;

/**
 * Guice {@link Provider} for generic DAO objects
 */
public class DAOProvider extends AbstractDynamicProvider {

  @Inject
  public DAOProvider(Injector injector) {
    super(injector);
  }

  @Override
  protected Object get(Class<?> clazz) {
    return DAO.dao(clazz);
  }
}