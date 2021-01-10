package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;

import com.google.common.collect.Iterators;

/**
 * Allows to chain multiple {@link IMethodHandler}s
 * @param <T> type of target/delegate object
 */
public class InvocationChain<T> implements IMethodHandler<T> {
	
	private static final InvocationChain<?> EMPTY = new InvocationChain<Object>(Collections.emptyList());
	
	private Iterator<IMethodHandler<T>> it;
	
	public InvocationChain(Iterable<IMethodHandler<T>> iterable) {
		it = iterable.iterator();
	}

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain) throws Throwable {
		if(chain==null || chain.equals(this)) {
			return handle(target, proxy, method, args);
		} else {
			return chain.handle(target, proxy, method, args, chain);
		}
	}
	
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
		return it.hasNext()?it.next().handle(target, proxy, method, args, this):null;
	}
	
	public InvocationChain<T> prepend(IMethodHandler<T>... prepand) {
		return prepend(Arrays.asList(prepand));
	}
	
	public InvocationChain<T> prepend(Iterable<IMethodHandler<T>> iterable) {
		this.it = Iterators.concat(iterable.iterator(), it);
		return this;
	}
	
	public InvocationChain<T> append(IMethodHandler<T>... prepand) {
		return append(Arrays.asList(prepand));
	}
	
	public InvocationChain<T> append(Iterable<IMethodHandler<T>> iterable) {
		this.it = Iterators.concat(it, iterable.iterator());
		return this;
	}
	
	public static <T> InvocationChain<T> empty() {
		return (InvocationChain<T>)EMPTY;
	}
}
