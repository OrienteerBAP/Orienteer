package org.orienteer.core.dao;

import org.orienteer.core.dao.handler.DefaultInterfaceMethodHandler;
import org.orienteer.core.dao.handler.QueryMethodHandler;
import org.orienteer.core.dao.handler.RetargetMethodHandler;
import org.orienteer.core.dao.handler.StackMethodHandler;

import java.lang.reflect.InvocationHandler;

/**
 * {@link InvocationHandler} for generic DAO interfaces
 */
class DAOInvocationHandler extends StackInvocationHandler<Object> {

  private static final StackMethodHandler<Object> STACK =
          new StackMethodHandler<>(
                  new RetargetMethodHandler<>(),
                  new DefaultInterfaceMethodHandler<>(),
                  new QueryMethodHandler<>()
          );

  public DAOInvocationHandler() {
    super(new Object(), STACK);
  }

}