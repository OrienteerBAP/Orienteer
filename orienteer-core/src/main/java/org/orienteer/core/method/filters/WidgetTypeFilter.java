package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;
import org.orienteer.core.module.OWidgetsModule;

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
