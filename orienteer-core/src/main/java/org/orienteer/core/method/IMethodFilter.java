package org.orienteer.core.method;

/**
 * 
 * Interface for all method filters
 *
 */

public interface IMethodFilter {
	/**
	 * Init data from filter definition
	 * Example for {@link SourceMethodDefinition} :
	 * 
	 *  @Method(order=10,filters = { 
	 *			@Filter(fClass = OClassBrowseFilter.class, fData = "OUser") 
	 *	})
	 * 
	 * There fData - input for "setFilterData" method 
	 * 
	 * @param filterData
	 */
	public void setFilterData(String filterData);
	/**
	 * Checks linked method for using in signed environment
	 * This method calls often - do not use hard calculation into 
	 * @param dataObject
	 * @return
	 */
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject);
}
