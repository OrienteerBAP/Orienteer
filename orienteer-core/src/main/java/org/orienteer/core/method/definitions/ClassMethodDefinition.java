package org.orienteer.core.method.definitions;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.method.MethodPlace;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.configs.JavaMethodOMethodConfig;
import org.orienteer.core.method.filters.OEntityFilter;
import org.orienteer.core.method.filters.PermissionFilter;
import org.orienteer.core.method.filters.SelectorFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * {@link IMethodDefinition} implementation for loading annotated java methods as IMethod objects
 *
 */
public class ClassMethodDefinition implements IMethodDefinition{

	private static final Logger LOG = LoggerFactory.getLogger(ClassMethodDefinition.class);
	
	private JavaMethodOMethodConfig config;
	
	public ClassMethodDefinition(java.lang.reflect.Method javaMethod) throws InstantiationException, IllegalAccessException {
		config = new JavaMethodOMethodConfig(javaMethod);

		config.filters().add(new SelectorFilter().setFilterData(config.selector().isEmpty()
																	?config.getJavaClass().getSimpleName()
																	:config.selector()));
		
		if (!config.permission().isEmpty()){
			config.filters().add(new PermissionFilter().setFilterData(config.permission()));
		}
	}

	@Override
	public String getMethodId() {
		return config.getMethodId();
	}

	@Override
	public IMethod getMethod(IMethodContext dataObject) {
		try {
			IMethod newMethod=null;
			if(MethodPlace.DATA_TABLE.equals(dataObject.getPlace())){
				newMethod = config.oClassTableMethodClass().newInstance();
			}else{
				newMethod = config.methodClass().newInstance();
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
		return config.order();
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


}
