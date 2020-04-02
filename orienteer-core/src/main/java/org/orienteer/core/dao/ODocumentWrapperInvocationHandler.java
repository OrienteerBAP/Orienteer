package org.orienteer.core.dao;

import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.handler.*;

import java.lang.reflect.InvocationHandler;

/**
 * {@link InvocationHandler} for {@link IODocumentWrapper}
 */
class ODocumentWrapperInvocationHandler extends StackInvocationHandler<ODocumentWrapper> {

  private static final StackMethodHandler<ODocumentWrapper> STACK =
					new StackMethodHandler<>(
									new MirrorMethodHandler<>(IODocumentWrapper.class),
									new RetargetMethodHandler<>(),
									new DefaultInterfaceMethodHandler<>(),
									new ODocumentGetHandler(),
									new ODocumentSetHandler(),
									new LookupMethodHandler(),
									new QueryMethodHandler<>(ODocumentWrapper::getDocument)
					);

  public ODocumentWrapperInvocationHandler(ODocumentWrapper wrapper) {
    super(wrapper, STACK);
  }

}