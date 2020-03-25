package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.dao.IMethodHandler.ResultHolder;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IMethodHandler} to SET field value to {@link ODocumentWrapper}
 */
public class ODocumentSetHandler implements IMethodHandler<ODocumentWrapper>{

	@Override
	public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().startsWith("set") && args.length==1) {
			target.getDocument().field(CommonUtils.decapitalize(method.getName().substring(3)), args[0]);
			return NULL_RESULT;
		}
		return null;
	}

}