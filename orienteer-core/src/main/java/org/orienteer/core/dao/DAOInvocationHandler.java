package org.orienteer.core.dao;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.List;

import org.orienteer.core.dao.handler.DAOHandlersMethodHandler;
import org.orienteer.core.dao.handler.DefaultInterfaceMethodHandler;
import org.orienteer.core.dao.handler.QueryMethodHandler;
import org.orienteer.core.dao.handler.RetargetMethodHandler;
import org.orienteer.core.dao.handler.StackMethodHandler;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link InvocationHandler} for generic DAO interfaces
 */
class DAOInvocationHandler extends StackInvocationHandler<Object> {
	
	private static final List<IMethodHandler<Object>> STACK = 
									Arrays.asList(
											new DAOHandlersMethodHandler<Object>(),
											new StackMethodHandler<Object>(
												new RetargetMethodHandler<Object>(),
												new DefaultInterfaceMethodHandler<Object>(),
												new QueryMethodHandler<Object>()
											)
											);
	
	public DAOInvocationHandler() {
		super(new Object(), STACK);
	}
	
}