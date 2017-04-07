package org.orienteer.core.method;

public interface IMethodFilter {
	public void setFilterData(String filterData);
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject);
}
