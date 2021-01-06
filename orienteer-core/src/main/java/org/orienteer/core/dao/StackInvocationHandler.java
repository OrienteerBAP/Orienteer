package org.orienteer.core.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.orienteer.core.dao.IMethodHandler.ResultHolder;
import org.orienteer.core.dao.handler.StackMethodHandler;

/**
 * {@link InvocationHandler} which use {@link StackMethodHandler} to handle invocation
 * @param <T>
 */
public class StackInvocationHandler<T> implements InvocationHandler {

	private static final Object[] NO_ARGS = new Object[0];
	
	private final T target;
	private StackMethodHandler<T> stack;
	
	public StackInvocationHandler(T target, IMethodHandler<T>... stack) {
		this(target, new StackMethodHandler<T>(stack));
	}
	
	public StackInvocationHandler(T target, StackMethodHandler<T> stack) {
		this.target = target;
		this.stack = stack;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(args==null) args = NO_ARGS;
		IMethodHandler.ResultHolder holder = stack.handle(target, proxy, method, args);
		if(holder!=null) return holder.result;
		else {
			String message = "Can't proxy method: "+method+"for target object "+target+
								" with proxy which implements: "+Arrays.deepToString(proxy.getClass().getInterfaces());
			throw new IllegalStateException(message);
		}
	}
	
	public T getTarget() {
		return target;
	}

}
