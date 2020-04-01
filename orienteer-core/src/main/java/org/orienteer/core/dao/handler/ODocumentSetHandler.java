package org.orienteer.core.dao.handler;

import java.lang.reflect.Method;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IMethodHandler} to SET field value to {@link ODocumentWrapper}
 */
public class ODocumentSetHandler extends AbstractMethodHandler<ODocumentWrapper> {

	@Override
	public ResultHolder handle(ODocumentWrapper target, Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().startsWith("set") && args.length==1) {
			String name=CommonUtils.decapitalize(method.getName().substring(3));
			DAOField fieldAnnotation = method.getAnnotation(DAOField.class);
			if(fieldAnnotation!=null && !Strings.isEmpty(fieldAnnotation.value())) name = fieldAnnotation.value();
			target.getDocument().field(name, args[0]);
			return returnChained(proxy, method);
		}
		return null;
	}

}