package org.orienteer.core.dao;

import org.orienteer.core.dao.handler.StackMethodHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * {@link InvocationHandler} which use {@link StackMethodHandler} to handle invocation
 *
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
    if (args == null) {
      args = NO_ARGS;
    }

    IMethodHandler.ResultHolder holder = stack.handle(target, proxy, method, args);
    if (holder != null) {
      if (holder.result != null) {
        return holder.result == target ? proxy : holder.result;
      }

      Class<?> returnClass = method.getReturnType();
      return returnClass != null && returnClass.isInstance(proxy) ? proxy : null;
    }

    throw new IllegalStateException("Can't proxy method: " + method);
  }

}
