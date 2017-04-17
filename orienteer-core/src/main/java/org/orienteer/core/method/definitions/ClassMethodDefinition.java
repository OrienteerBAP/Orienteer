package org.orienteer.core.method.definitions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.IClassMethod;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.filters.OEntityFilter;

/**
 * 
 * {@link IMethodDefinition} implementation for loading annotated java methods as IMethod objects
 *
 */
public class ClassMethodDefinition implements IMethodDefinition{

	private int order;
	private String methodId;
	private Class<? extends IMethod> methodClass;
	private String oClassName;
	private List<IMethodFilter> filters;
	private Method javaMethod;

	public ClassMethodDefinition(java.lang.reflect.Method javaMethod) throws InstantiationException, IllegalAccessException {
		ClassOMethod methodAnnotation = javaMethod.getAnnotation(ClassOMethod.class);
		order = methodAnnotation.order();
		methodId = javaMethod.getDeclaringClass().getSimpleName()+"."+javaMethod.getName();
		methodClass = methodAnnotation.methodClass();
		oClassName = javaMethod.getDeclaringClass().getSimpleName();
		filters = new ArrayList<IMethodFilter>();
		this.javaMethod = javaMethod;
		
		if (methodAnnotation.filters().length>0){
			filters = new ArrayList<IMethodFilter>();
			for (OFilter iMethodFilter : methodAnnotation.filters()) {
				IMethodFilter newFilter = iMethodFilter.fClass().newInstance();
				newFilter.setFilterData(iMethodFilter.fData());
				filters.add(newFilter);
			}
		}
		filters.add(new OEntityFilter().setFilterData(oClassName));

	}

	@Override
	public String getMethodId() {
		return methodId;
	}

	@Override
	public IMethod getMethod(IMethodEnvironmentData dataObject) {
		try {
			if (IClassMethod.class.isAssignableFrom(methodClass)){
				IMethod newMethod = methodClass.newInstance();
				((IClassMethod)newMethod).initOClassMethod(javaMethod);
				return newMethod;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (filters!=null){
			for (IMethodFilter iMethodFilter : filters) {
				if (!iMethodFilter.isSupportedMethod(dataObject)){
					return false;
				}
			}
		}
		return true;
	}


}
