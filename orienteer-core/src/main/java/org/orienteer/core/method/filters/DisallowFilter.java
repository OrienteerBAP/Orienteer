package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * Always disallow filter
 *
 */
public class DisallowFilter implements IMethodFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		return false;
	}

	@Override
	public void setFilterData(String filterData) {
		
	}

}
