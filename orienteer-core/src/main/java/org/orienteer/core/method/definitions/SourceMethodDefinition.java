package org.orienteer.core.method.definitions;

import java.util.ArrayList;
import java.util.List;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.Method;
import org.orienteer.core.method.Filter;
/**
 * {@link IMethodDefinition} implementation for Java source method definitions
 * 
 * Using annotation {@link Method} for define metadata. 
 * 
 *
 */
public class SourceMethodDefinition implements IMethodDefinition{
	private Class<? extends IMethod> methodClass;
	private List<IMethodFilter> filters;
	private int order;
	private String methodId;
	
	
	public static boolean isSupportedClass(Class<? extends IMethod> methodClass){
		if (methodClass.isAnnotationPresent(Method.class)){
			return true;
		}
		return false;
	} 
	
	public SourceMethodDefinition(Class<? extends IMethod> methodClass) throws InstantiationException, IllegalAccessException {
		this.methodClass = methodClass;
		Method methodAnnotation = methodClass.getAnnotation(Method.class);
		if (methodAnnotation.filters().length>0){
			filters = new ArrayList<IMethodFilter>();
			for (Filter iMethodFilter : methodAnnotation.filters()) {
				IMethodFilter newFilter = iMethodFilter.fClass().newInstance();
				newFilter.setFilterData(iMethodFilter.fData());
				filters.add(newFilter);
			}
		}
		order = methodAnnotation.order();
		methodId = methodClass.getName();
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
		if (filters!=null){
			for (IMethodFilter iMethodFilter : filters) {
				if (!iMethodFilter.isSupportedMethod(dataObject)){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String getMethodId() {
		return methodId;
	}

	@Override
	public int getOrder() {
		return order;
	}

}
