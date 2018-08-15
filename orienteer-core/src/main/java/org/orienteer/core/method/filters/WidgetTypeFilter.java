package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodContext;

/**
 * 
 * Widget type filter. Support regexp patterns.
 *
 */

public class WidgetTypeFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		if (filterData!=null && dataObject.getCurrentWidgetType()!=null){
			return dataObject.getCurrentWidgetType().matches(filterData);
		}
		return false;
	}

}
