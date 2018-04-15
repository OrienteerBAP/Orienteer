package org.orienteer.core.method;

/**
 * 
 * Interface for all method filters
 *
 */

public interface IMethodFilter{
	/**
	 * Init data from filter definition
	 * Example :
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
	 * @param context method context
	 * @return true if method supported
	 */
	public boolean isSupportedMethod(IMethodContext context);
}
