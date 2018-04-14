package org.orienteer.core.method.configs;

import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.OMethod;

public class JavaClassOMethodConfig extends AbstractOMethodConfig{

	public JavaClassOMethodConfig(Class<? extends IMethod> methodClass) {
		super(methodClass.getSimpleName(), methodClass.getAnnotation(OMethod.class));
	}


}
