package org.orienteer.core.method;

public class TestFilter implements IMethodFilter{

	String filterData;
	
	@Override
	public void setFilterData(String filterData) {
		this.filterData = filterData;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		return (dataObject.getCurrentWidget()==null && 
				dataObject.getDisplayModeModel()==null && 
				dataObject.getDisplayObjectModel()==null &&
				filterData.equals("testData")
				);
	}


}
