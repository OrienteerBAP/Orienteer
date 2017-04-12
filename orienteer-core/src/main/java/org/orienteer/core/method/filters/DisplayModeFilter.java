package org.orienteer.core.method.filters;

import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * {@link DisplayMode} filter
 *
 */

public class DisplayModeFilter implements IMethodFilter{

	DisplayMode filterData;
	
	@Override
	public void setFilterData(String filterData) {
		this.filterData = DisplayMode.valueOf(filterData);
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		if (dataObject.getDisplayModeModel()!=null){
			return filterData.equals(dataObject.getDisplayModeModel().getObject());
		}
		return false;
	}
}
