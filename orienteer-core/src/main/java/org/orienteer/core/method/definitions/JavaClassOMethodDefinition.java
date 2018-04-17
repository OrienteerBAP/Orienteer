package org.orienteer.core.method.definitions;

import java.util.List;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.SelectorFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaClassOMethodDefinition extends AbstractOMethodDefinition{
	
	private static final Logger LOG = LoggerFactory.getLogger(JavaClassOMethodDefinition.class);

	private Class<? extends IMethod> javaClass;
	
	public static boolean isSupportedClass(Class<?> methodClass){
		return IMethod.class.isAssignableFrom(methodClass);
	}
	
	public JavaClassOMethodDefinition(Class<? extends IMethod> methodClass) {
		super(methodClass.getSimpleName(), methodClass.getAnnotation(OMethod.class));
		this.javaClass = methodClass;
	}
	
	@Override
	public IMethod getMethod(IMethodContext context) {
		try {
			IMethod newMethod = javaClass.newInstance();
			newMethod.init(this,context);
			return newMethod;
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Can't obtain a method", e);
		}
		return null;
	}
	
}
