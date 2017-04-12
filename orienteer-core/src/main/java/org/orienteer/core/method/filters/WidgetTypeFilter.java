package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * Widget type filter. Support regexp patterns.
 *
 */

public class WidgetTypeFilter implements IMethodFilter{

	String filterData; 
	@Override
	public void setFilterData(String filterData) {
		this.filterData = filterData ;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (filterData!=null && dataObject.getCurrentWidgetType()!=null){
			return dataObject.getCurrentWidgetType().matches(filterData);
		}
		return false;
	}

}
