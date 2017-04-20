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
	 *  @OMethod(order=10,filters = { 
	 *			@OFilter(fClass = OClassBrowseFilter.class, fData = "OUser") 
	 *	})
	 * 
	 * There fData - input for "setFilterData" method 
	 * 
	 * @param filterData
	 */
	public IMethodFilter setFilterData(String filterData);
	/**
	 * Checks linked method for using in assigned environment
	 * This method calls often - do not use hard calculation into 
	 * @param dataObject
	 * @return
	 */
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject);
}
