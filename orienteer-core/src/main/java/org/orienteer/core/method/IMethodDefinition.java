package org.orienteer.core.method;

/**
 * 
 * Precise method definition with filters and other things 
 *
 */
public interface IMethodDefinition {
	/**
	 * Returning method id. Should be unique.
	 * @return id for a method
	 */
	public String getMethodId();
	/**
	 * Return method instance by input data 
	 * @param dataObject environment data object
	 * @return {@link IMethod}
	 */
	public IMethod getMethod(IMethodContext dataObject);
	/**
	 * Get method order in methods list
	 * @return method order
	 */
	public int getOrder();
	/**
	 * Check method for using in this environment by method filters
	 * Calls before {@link IMethodDefinition#getMethod(IMethodContext)} 
	 * @param dataObject
	 * @return true if method supported
	 */
	public boolean isSupportedMethod(IMethodContext dataObject);

}
