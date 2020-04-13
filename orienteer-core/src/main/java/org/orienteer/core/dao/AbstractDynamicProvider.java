package org.orienteer.core.dao;

import java.lang.reflect.Method;

import org.joor.Reflect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Dependency;

/**
 * {@link Provider} which can obtain dynamically required type from Guice context
 */
public abstract class AbstractDynamicProvider implements Provider<Object> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDynamicProvider.class);

	private Injector injector;
	
	@Inject
	public AbstractDynamicProvider(Injector injector) {
		this.injector = injector;
	}

	@SuppressWarnings("unchecked")
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
