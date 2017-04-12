package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodFilter;

/**
 * 
 * Filter stub for pure string filters
 *
 */
public abstract class AbstractStringFilter implements IMethodFilter{

	protected String filterData;
	
	@Override
	public void setFilterData(String filterData) {
		this.filterData = filterData;
	}
}
