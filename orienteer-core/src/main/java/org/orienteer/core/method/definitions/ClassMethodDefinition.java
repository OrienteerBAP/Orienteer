package org.orienteer.core.method.definitions;

import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.MethodPlace;
import org.orienteer.core.method.configs.OClassOMethodConfig;
import org.orienteer.core.method.filters.OEntityFilter;
import org.orienteer.core.method.filters.PermissionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@link IMethodDefinition} implementation for loading annotated java methods as IMethod objects
 *
 */
public class ClassMethodDefinition implements IMethodDefinition{

	private static final Logger LOG = LoggerFactory.getLogger(ClassMethodDefinition.class);
	
	private int order;
	private String methodId;
	private Class<? extends IMethod> methodClass;
	private Class<? extends IMethod> groupMethodClass;
	private String oClassName;
	private OClassOMethodConfig config;
	
	public ClassMethodDefinition(java.lang.reflect.Method javaMethod) throws InstantiationException, IllegalAccessException {
		ClassOMethod methodAnnotation = javaMethod.getAnnotation(ClassOMethod.class);
		order = methodAnnotation.order();
		methodId = javaMethod.getDeclaringClass().getSimpleName()+"."+javaMethod.getName();
		methodClass = methodAnnotation.methodClass();
		groupMethodClass = methodAnnotation.oClassTableMethodClass();
		oClassName = javaMethod.getDeclaringClass().getSimpleName();
		
		config = new OClassOMethodConfig(methodAnnotation,javaMethod);
		

		config.filters().add(new OEntityFilter().setFilterData(oClassName));
		
		if (!methodAnnotation.permission().isEmpty()){
			config.filters().add(new PermissionFilter().setFilterData(methodAnnotation.permission()));
		}
	}

	@Override
	public String getMethodId() {
		return methodId;
	}

	@Override
	public IMethod getMethod(IMethodEnvironmentData dataObject) {
		try {
			IMethod newMethod=null;
			if(MethodPlace.DATA_TABLE.equals(dataObject.getPlace())){
				newMethod = groupMethodClass.newInstance();
			}else{
				newMethod = methodClass.newInstance();
			}
			if (newMethod!=null){
				newMethod.methodInit(getMethodId(), dataObject, config);
				return newMethod;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Can't obtain a method", e);
		}
		return null;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (config.filters()!=null){
			for (IMethodFilter iMethodFilter : config.filters()) {
				if (!iMethodFilter.isSupportedMethod(dataObject)){
					return false;
				}
			}
		}
		return true;
	}


}
