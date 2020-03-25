package org.orienteer.core.dao.handler;

import org.orienteer.core.dao.IMethodHandler;

import java.lang.reflect.Method;

/**
 * {@link IMethodHandler} which retarget method call if target/delegate supports it
 *
 * @param <T> type of target/delegate object
 */
public class RetargetMethodHandler<T> implements IMethodHandler<T> {

  @Override
  public ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
    Class<?> declaringClass = method.getDeclaringClass();
    if (declaringClass.isInstance(target)) {
    	Object resultObj = method.invoke(target, args);
      return new ResultHolder(resultObj);
    }

    return null;
  }

}
