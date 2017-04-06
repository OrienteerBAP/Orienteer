package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

public class AllowFilter implements IMethodFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		return true;
	}


}
