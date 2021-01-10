package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;
import java.util.Optional;

import org.orienteer.core.dao.IMethodHandler;

/**
 * {@link IMethodHandler} which use stacked other {@link IMethodHandler}s
 * It's useful to make invocations more flat and easier to debug
 * @param <T>  type of target/delegate object
 */
public class StackMethodHandler<T> implements IMethodHandler<T> {
	private final IMethodHandler<T>[] stack;
	
	@SafeVarargs
	public StackMethodHandler(IMethodHandler<T>... stack) {
		this.stack = stack;
	}

	@Override
	public Optional<Object> handle(T target, Object proxy, Method method, Object[] args, InvocationChain<T> chain) throws Throwable {
		Optional<Object> holder = null;
		for (IMethodHandler<T> handler : stack) {
			holder=handler.handle(target, proxy, method, args, InvocationChain.empty());
			if(holder!=null) return holder;
		}
		return chain.handle(target, proxy, method, args);
	}
	
}
