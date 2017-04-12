package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * Always disallow filter
 *
 */
public class DisallowFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		return false;
	}
}
