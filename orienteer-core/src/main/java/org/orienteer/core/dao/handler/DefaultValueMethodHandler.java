package org.orienteer.core.dao.handler;

import static com.google.common.primitives.Primitives.wrap;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.wicket.core.util.lang.WicketObjects;
import org.apache.wicket.util.io.IClusterable;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.joor.Reflect;
import org.orienteer.core.dao.DAODefaultValue;
import org.orienteer.core.dao.IMethodHandler;

/**
 * Handler which returns default value if it was provided by annotation {@link DAODefaultValue}
 * @param <T> type of target/delegate object
 */
public class DefaultValueMethodHandler<T> implements IMethodHandler<T> {
	
	/**
	 * Interface which helps to customize generation of default value
	 */
	public static interface IDefaultValueProvider extends IClusterable {
		public Object provide(Class<?> targetClass, String defaultValueSeed);
	}
	
	/**
	 * Implementation of {@link IDefaultValueProvider} which creates
	 * default value through constructor with String value
	 */
	public static class DefaultValueProvider implements IDefaultValueProvider {
		@Override
		public Object provide(Class<?> targetClass, String defaultValueSeed) {
			return Reflect.onClass(wrap(targetClass)).create(defaultValueSeed).get();
		}
	}
	
	private final Map<Class<? extends IDefaultValueProvider>, IDefaultValueProvider> cache = new HashMap<>(8);

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain)
			throws Throwable {
		Optional<Object> result = chain.handle(target, proxy, method, args);
		if(result==null || !result.isPresent()) {
			DAODefaultValue defaultValue = method.getAnnotation(DAODefaultValue.class);
			if(defaultValue!=null) {
				Class<? extends IDefaultValueProvider> providerClass = defaultValue.provider();
				IDefaultValueProvider provider = cache.computeIfAbsent(providerClass, (c) -> Reflect.onClass(c).create().get());
				result = Optional.of(provider.provide(method.getReturnType(), defaultValue.value()));
			}
		}
		return result;
	}

}
