package org.orienteer.core.dao;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joor.Reflect;
import org.orienteer.core.dao.handler.DAOHandlersMethodHandler;
import org.orienteer.core.dao.handler.DefaultInterfaceMethodHandler;
import org.orienteer.core.dao.handler.DefaultValueMethodHandler;
import org.orienteer.core.dao.handler.EqualsMethodHandler;
import org.orienteer.core.dao.handler.LookupMethodHandler;
import org.orienteer.core.dao.handler.MirrorMethodHandler;
import org.orienteer.core.dao.handler.ODocumentGetHandler;
import org.orienteer.core.dao.handler.ODocumentSetHandler;
import org.orienteer.core.dao.handler.ExecuteSQLMethodHandler;
import org.orienteer.core.dao.handler.RetargetMethodHandler;
import org.orienteer.core.dao.handler.StackMethodHandler;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link InvocationHandler} for {@link IODocumentWrapper}
 */
class ODocumentWrapperInvocationHandler extends StackInvocationHandler<ODocumentWrapper> {
	
	
	private static final List<IMethodHandler<ODocumentWrapper>> STACK = Arrays.asList(
											new DefaultValueMethodHandler<ODocumentWrapper>(),
											new DAOHandlersMethodHandler<ODocumentWrapper>(),
											new StackMethodHandler<ODocumentWrapper>(
												new EqualsMethodHandler<ODocumentWrapper>(),
												new MirrorMethodHandler<ODocumentWrapper>(IODocumentWrapper.class),
												new RetargetMethodHandler<ODocumentWrapper>(),
												new DefaultInterfaceMethodHandler<ODocumentWrapper>(),
												new ODocumentGetHandler(),
												new ODocumentSetHandler(),
												new LookupMethodHandler(),
												new ExecuteSQLMethodHandler<ODocumentWrapper>(ODocumentWrapper::getDocument)
											)
										);
	
	public ODocumentWrapperInvocationHandler(ODocumentWrapper wrapper) {
		super(wrapper, STACK);
	}
	
}