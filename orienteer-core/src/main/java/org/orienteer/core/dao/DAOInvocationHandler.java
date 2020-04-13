package org.orienteer.core.dao;

import java.lang.reflect.InvocationHandler;

import org.orienteer.core.dao.handler.DefaultInterfaceMethodHandler;
import org.orienteer.core.dao.handler.QueryMethodHandler;
import org.orienteer.core.dao.handler.RetargetMethodHandler;
import org.orienteer.core.dao.handler.StackMethodHandler;

/**
 * {@link InvocationHandler} for generic DAO interfaces
 */
class DAOInvocationHandler extends StackInvocationHandler<Object> {
	
	private static final StackMethodHandler<Object> STACK = 
									new StackMethodHandler<Object>(
											new RetargetMethodHandler<Object>(),
											new DefaultInterfaceMethodHandler<Object>(),
											new QueryMethodHandler<Object>()
											);
	
	public DAOInvocationHandler() {
		super(new Object(), STACK);
	}
	
}