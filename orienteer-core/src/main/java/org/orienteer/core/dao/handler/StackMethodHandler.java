package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IMethodHandler.ResultHolder;

/**
 * {@link IMethodHandler} which use stacked other {@link IMethodHandler}s
 * @param <T>  type of target/delegate object
 */
public class StackMethodHandler<T> implements IMethodHandler<T> {
	private final IMethodHandler<T>[] stack;
	
	@SafeVarargs
	public StackMethodHandler(IMethodHandler<T>... stack) {
		this.stack = stack;
	}

	@Override
	public ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
		ResultHolder holder = null;
		for (IMethodHandler<T> handler : stack) {
			if((holder=handler.handle(target, proxy, method, args))!=null) break;
		}
		return holder;
	}
	
}
