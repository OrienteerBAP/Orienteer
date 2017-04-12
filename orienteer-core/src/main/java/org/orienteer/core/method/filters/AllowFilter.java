package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * Always allow filter.
 *
 */
public class AllowFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		return true;
	}

}
