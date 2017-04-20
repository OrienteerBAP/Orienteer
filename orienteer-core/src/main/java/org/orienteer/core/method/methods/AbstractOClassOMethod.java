package org.orienteer.core.method.methods;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IClassMethod;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;

/**
 * 
 * @author Asm
 *
 */

public abstract class AbstractOClassOMethod implements Serializable,IMethod,IClassMethod{

	private static final long serialVersionUID = 1L;
	protected IMethodEnvironmentData envData;
	protected String id;
	protected String javaMethodName;
	protected String javaClassName;
	protected ClassOMethod annotation;

	@Override
	public void initOClassMethod(Method javaMethod) {
		this.javaMethodName = javaMethod.getName();
		this.javaClassName = javaMethod.getDeclaringClass().getName();
		this.annotation = javaMethod.getAnnotation(ClassOMethod.class);
		
	}

	@Override
	public void methodInit(String id,IMethodEnvironmentData envData) {
		this.envData = envData;
		this.id = id;
	}

}
