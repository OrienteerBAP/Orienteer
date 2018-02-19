package org.orienteer.core.method;

import org.orienteer.core.method.definitions.SourceMethodDefinition;

/**
 * 
 * Interface for all method filters
 *
 */

public interface IMethodFilter{
	/**
	 * Init data from filter definition
	 * Example for {@link SourceMethodDefinition} :
	 * <pre>
	 *  &#64;OMethod(order=10,filters = { 
	 *			&#64;OFilter(fClass = OClassBrowseFilter.class, fData = "OUser") 
	 *	})
	 * </pre>
	 * There fData - input for "setFilterData" method 
	 * 
	 * @param filterData - criteria to filter
	 * @return filter
	 */
	public IMethodFilter setFilterData(String filterData);
	/**
	 * Checks linked method for using in assigned environment
	 * This method calls often - do not use hard calculation into 
	 * @param dataObject
	 * @return true if method supported
	 */
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject);
}
