package org.orienteer.core.method.definitions;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.configs.AbstractOMethodConfig;
import org.orienteer.core.method.configs.JavaClassOMethodConfig;
import org.orienteer.core.method.filters.PermissionFilter;
import org.orienteer.core.method.filters.SelectorFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * {@link IMethodDefinition} implementation for Java source method definitions
 * 
 * Using annotation {@link OMethod} for define metadata. 
 * 
 *
 */
public class SourceMethodDefinition implements IMethodDefinition{
	
	private static final Logger LOG = LoggerFactory.getLogger(SourceMethodDefinition.class);
	private Class<? extends IMethod> methodClass;
	private JavaClassOMethodConfig config;
	
	
	public static boolean isSupportedClass(Class<? extends IMethod> methodClass){
		return methodClass.isAnnotationPresent(OMethod.class);
	} 
	
	public SourceMethodDefinition(Class<? extends IMethod> methodClass) throws InstantiationException, IllegalAccessException {
		this.methodClass = methodClass;
		config = new JavaClassOMethodConfig(methodClass);
		
		if (!config.selector().isEmpty()){
			config.filters().add(new SelectorFilter().setFilterData(config.selector()));
		}
		if (!config.permission().isEmpty()){
			config.filters().add(new PermissionFilter().setFilterData(config.permission()));
		}
	}

	@Override
	public IMethod getMethod(IMethodContext dataObject) {
		try {
			IMethod newMethod = methodClass.newInstance();
			newMethod.methodInit(getMethodId(),dataObject,config);
			return newMethod;
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Can't obtain a method", e);
		}
		return null;
	}

	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		if (config.filters()!=null){
			for (IMethodFilter iMethodFilter : config.filters()) {
				if (!iMethodFilter.isSupportedMethod(dataObject)){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String getMethodId() {
		return config.getMethodId();
	}

	@Override
	public int getOrder() {
		return config.order();
	}

}
