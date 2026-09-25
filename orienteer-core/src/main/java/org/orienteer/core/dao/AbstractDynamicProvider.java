package org.orienteer.core.dao;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.ProvisionListener;

/**
 * {@link Provider} which can obtain dynamically required type from Guice context.
 * The required type is the type annotated with {@link ProvidedBy} that points to a subclass of this provider.
 * It's tracked through a {@link ProvisionListener}, which must be bound with {@link #bindProvisionListener(Binder)}
 * in the top-most injector that can create such bindings.
 */
public abstract class AbstractDynamicProvider implements Provider<Object> {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDynamicProvider.class);
	
	private static final ThreadLocal<Deque<Class<?>>> REQUIRED_TYPES = ThreadLocal.withInitial(ArrayDeque::new);
	
	/**
	 * Matches bindings of types which are {@link ProvidedBy} a subclass of {@link AbstractDynamicProvider}
	 */
	private static final AbstractMatcher<Binding<?>> DYNAMICALLY_PROVIDED = new AbstractMatcher<Binding<?>>() {
		@Override
		public boolean matches(Binding<?> binding) {
			ProvidedBy providedBy = binding.getKey().getTypeLiteral().getRawType().getAnnotation(ProvidedBy.class);
			return providedBy!=null && AbstractDynamicProvider.class.isAssignableFrom(providedBy.value());
		}
	};
	
	/**
	 * Remembers the type being provisioned, so {@link AbstractDynamicProvider#get()} knows what to create
	 */
	private static final ProvisionListener TRACK_REQUIRED_TYPE = new ProvisionListener() {
		@Override
		public <T> void onProvision(ProvisionInvocation<T> provision) {
			Deque<Class<?>> requiredTypes = REQUIRED_TYPES.get();
			requiredTypes.push(provision.getBinding().getKey().getTypeLiteral().getRawType());
			try {
				provision.provision();
			} finally {
				requiredTypes.pop();
			}
		}
	};

	private Injector injector;
	
	@Inject
	public AbstractDynamicProvider(Injector injector) {
		this.injector = injector;
	}
	
	/**
	 * Binds the {@link ProvisionListener} which lets dynamic providers know the required type
	 * @param binder binder to use
	 */
	public static void bindProvisionListener(Binder binder) {
		binder.bindListener(DYNAMICALLY_PROVIDED, TRACK_REQUIRED_TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Object get() {
		return get(getRequiredType());
	}
	
	protected abstract Object get(Class<?> clazz);
	
    private Class<?> getRequiredType() {
    	Class<?> requiredType = REQUIRED_TYPES.get().peek();
    	if(requiredType==null) {
    		throw new ProvisionException(getClass().getName()+" can only provide types annotated with @ProvidedBy("
    									+getClass().getSimpleName()+".class): the required type is unknown. "
    									+"Is AbstractDynamicProvider.bindProvisionListener() called in the injector?");
    	}
    	return requiredType;
    }
    
}
