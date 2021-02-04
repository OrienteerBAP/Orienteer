package org.orienteer.core.dao.handler;

import static com.google.common.primitives.Primitives.wrap;

import java.lang.reflect.Method;
import java.util.Optional;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.IMethodHandler;
import org.orienteer.core.util.CommonUtils;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IMethodHandler} to GET field value from {@link ODocumentWrapper}
 */
public class ODocumentGetHandler extends AbstractMethodHandler<ODocumentWrapper>{

	@Override
	public Optional<Object> handle(ODocumentWrapper target, Object proxy, Method method, Object[] args, InvocationChain<ODocumentWrapper> chain) throws Throwable {
		if(args.length==0) {
			String name=null;
			String methodName = method.getName();
			if(methodName.startsWith("get")) name = CommonUtils.decapitalize(methodName.substring(3));
			if(methodName.startsWith("is")) name = CommonUtils.decapitalize(methodName.substring(2));
			DAOField fieldAnnotation = method.getAnnotation(DAOField.class);
			if(fieldAnnotation!=null && !Strings.isEmpty(fieldAnnotation.value())) name = fieldAnnotation.value();
			if(name!=null) {
				Object value = target.getDocument().field(name, wrap(method.getReturnType()));
				return Optional.ofNullable(prepareForJava(value, method));
			}
		}
		return chain.handle(target, proxy, method, args);
	}

}
