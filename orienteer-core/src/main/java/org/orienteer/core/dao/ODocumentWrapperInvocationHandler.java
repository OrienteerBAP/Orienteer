package org.orienteer.core.dao;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.joor.Reflect;
import org.orienteer.core.dao.handler.DefaultInterfaceMethodHandler;
import org.orienteer.core.dao.handler.EqualsMethodHandler;
import org.orienteer.core.dao.handler.LookupMethodHandler;
import org.orienteer.core.dao.handler.MirrorMethodHandler;
import org.orienteer.core.dao.handler.ODocumentGetHandler;
import org.orienteer.core.dao.handler.ODocumentSetHandler;
import org.orienteer.core.dao.handler.QueryMethodHandler;
import org.orienteer.core.dao.handler.RetargetMethodHandler;
import org.orienteer.core.dao.handler.StackMethodHandler;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link InvocationHandler} for {@link IODocumentWrapper}
 */
class ODocumentWrapperInvocationHandler extends StackInvocationHandler<ODocumentWrapper> {
	
	private static final StackMethodHandler<ODocumentWrapper> STACK = 
									new StackMethodHandler<ODocumentWrapper>(
											new EqualsMethodHandler<ODocumentWrapper>(),
											new MirrorMethodHandler<ODocumentWrapper>(IODocumentWrapper.class),
											new RetargetMethodHandler<ODocumentWrapper>(),
											new DefaultInterfaceMethodHandler<ODocumentWrapper>(),
											new ODocumentGetHandler(),
											new ODocumentSetHandler(),
											new LookupMethodHandler(),
											new QueryMethodHandler<ODocumentWrapper>(ODocumentWrapper::getDocument)
											);
	
	public ODocumentWrapperInvocationHandler(ODocumentWrapper wrapper) {
		super(wrapper, STACK);
	}
	
}