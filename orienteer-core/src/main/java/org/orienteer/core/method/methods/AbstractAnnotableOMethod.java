package org.orienteer.core.method.methods;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.OMethod;

/**
 * Base class for pure OMethods.
 * Using {@link OMethod} annotation
 * 
 */
public abstract class AbstractAnnotableOMethod extends AbstractOMethod{
	private static final long serialVersionUID = 1L;
	
	private OMethod annotation;
	
	@Override
	public void methodInit(String id, IMethodEnvironmentData envData) {
		super.methodInit(id,envData);
		annotation = this.getClass().getAnnotation(OMethod.class);
	}
	
	@Override
	protected String getTitleKey() {
		return annotation.titleKey();
	}
	
	protected OMethod getAnnotation() {
		return annotation;
	}
}
