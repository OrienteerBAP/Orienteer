package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodContext;

/**
 * 
 * Always disallow filter
 *
 */
public class DisallowFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		return false;
	}
}
