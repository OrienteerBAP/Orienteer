package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * Always allow filter.
 *
 */
public class AllowFilter implements IMethodFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		return true;
	}

	@Override
	public void setFilterData(String filterData) {
		
	}


}
