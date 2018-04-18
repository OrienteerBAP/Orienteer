package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodContext;

/**
 * 
 * Always allow filter.
 *
 */
public class AllowFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		return true;
	}

}
