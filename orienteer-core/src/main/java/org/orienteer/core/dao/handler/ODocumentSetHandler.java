package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.util.CommonUtils;

import java.lang.reflect.Method;

/**
 * {@link IMethodHandler} to SET field value to {@link ODocumentWrapper}
 */
public class ODocumentSetHandler implements IMethodHandler<ODocumentWrapper> {

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().startsWith("set") && args.length == 1) {
    	String fieldName = CommonUtils.decapitalize(method.getName().substring(3));
      target.getDocument().field(fieldName, args[0]);
      return NULL_RESULT;
    }

    return null;
  }

}