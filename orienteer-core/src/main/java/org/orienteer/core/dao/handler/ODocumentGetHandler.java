package org.orienteer.core.dao.handler;

import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.util.CommonUtils;

import java.lang.reflect.Method;

/**
 * {@link IMethodHandler} to GET field value from {@link ODocumentWrapper}
 */
public class ODocumentGetHandler implements IMethodHandler<ODocumentWrapper> {

  @Override
  public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().startsWith("get") && args.length == 0) {
			String fieldName = CommonUtils.decapitalize(method.getName().substring(3));
			Object resultObj = target.getDocument().field(fieldName);
			return new ResultHolder(resultObj);
		}

    return null;
  }

}
