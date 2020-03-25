package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;

import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IMethodHandler} to GET field value from {@link ODocumentWrapper}
 */
public class ODocumentGetHandler implements IMethodHandler<ODocumentWrapper>{

	@Override
	public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().startsWith("get") && args.length==0) {
			return new ResultHolder(target.getDocument().field(CommonUtils.decapitalize(method.getName().substring(3))));
		} else return null;
	}

}
