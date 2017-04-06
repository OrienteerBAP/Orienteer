package org.orienteer.core.method.definitions;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.Method;

public class SourceMethodDefinition implements IMethodDefinition{
	Class<? extends IMethod> methodClass;
	IMethodFilter filter;
	
	public SourceMethodDefinition(Class<? extends IMethod> methodClass) {
		this.methodClass = methodClass;
		if (methodClass.isAnnotationPresent(Method.class)){
			Method methodAnnotation = methodClass.getAnnotation(Method.class);
			try {
				filter = methodAnnotation.filter().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public IMethod getMethod(IMethodEnvironmentData dataObject) {
		try {
			return methodClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (filter!=null){
			return filter.isSupportedMethod(dataObject);
		}else{
			return true;
		}
	}

}
