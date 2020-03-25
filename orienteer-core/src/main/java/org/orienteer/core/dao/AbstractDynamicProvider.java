package org.orienteer.core.dao;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.spi.Dependency;
import org.joor.Reflect;

/**
 * {@link Provider} which can obtain dynamically required type from Guice context
 */
public abstract class AbstractDynamicProvider implements Provider<Object> {

  private Injector injector;

  @Inject
  public AbstractDynamicProvider(Injector injector) {
    this.injector = injector;
  }

  @Override
  public final Object get() {
    return get(getRequiredType());
  }

  protected abstract Object get(Class<?> clazz);

  private Class<?> getRequiredType() {

    Reflect context = Reflect.on(injector).call("enterContext");
    try {
      Dependency<?> dependency = context.call("getDependency").get();
      return dependency.getKey().getTypeLiteral().getRawType();
    } finally {
      context.call("close");
    }
  }

}
