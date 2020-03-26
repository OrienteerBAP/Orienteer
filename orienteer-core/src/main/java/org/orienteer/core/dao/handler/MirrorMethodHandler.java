package org.orienteer.core.dao.handler;

import org.orienteer.core.dao.IMethodHandler;

import java.lang.reflect.Method;

/**
 * {@link IMethodHandler} to mirror invokations between interface and target/delegate just by method name and args
 *
 * @param <T> type of target/delegate object
 */
public class MirrorMethodHandler<T> implements IMethodHandler<T> {

  private final Class<?> mirrorInterface;

  public MirrorMethodHandler(Class<?> mirrorInterface) {
    this.mirrorInterface = mirrorInterface;
  }

  @Override
  public ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getDeclaringClass().equals(mirrorInterface)) {
      Object ret = target.getClass()
              .getMethod(method.getName(), method.getParameterTypes())
              .invoke(target, args);
      return new ResultHolder(target == ret ? proxy : ret);
    }

    return null;
  }

}
