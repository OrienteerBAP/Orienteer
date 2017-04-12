package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;

/**
 * 
 * Widget type filter. Support regexp patterns.
 *
 */

public class WidgetTypeFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (filterData!=null && dataObject.getCurrentWidgetType()!=null){
			return dataObject.getCurrentWidgetType().matches(filterData);
		}
		return false;
	}

}
