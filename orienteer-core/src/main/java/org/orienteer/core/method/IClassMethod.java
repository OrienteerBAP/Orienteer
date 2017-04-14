package org.orienteer.core.method;

/**
 * {@link IMethod} with ability load self from annotated({@link ClassMethod}) Java object fields
 *
 */

public interface IClassMethod extends IMethod{
	public void initOClassMethod(java.lang.reflect.Method javaMethod,String methodId);
}
