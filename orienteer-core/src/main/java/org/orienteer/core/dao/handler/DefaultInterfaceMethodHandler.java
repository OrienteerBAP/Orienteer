package org.orienteer.core.dao.handler;

import org.joor.Reflect;
import org.orienteer.core.dao.IMethodHandler;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * {@link IMethodHandler} which invoke interfaces default method implementations
 *
 * @param <T> type of target/delegate object
 */
public class DefaultInterfaceMethodHandler<T> implements IMethodHandler<T> {

  @Override
  public ResultHolder handle(T target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.isDefault()) {
      Class<?> declaringClass = method.getDeclaringClass();
      Reflect reflect = Reflect.onClass(MethodHandles.Lookup.class);
      MethodHandles.Lookup lookup = reflect.create(declaringClass, MethodHandles.Lookup.PRIVATE).get();
      Object resultObj = lookup.unreflectSpecial(method, declaringClass)
              .bindTo(proxy)
              .invokeWithArguments(args);

      return new ResultHolder(resultObj);
    }

    return null;
  }

}
