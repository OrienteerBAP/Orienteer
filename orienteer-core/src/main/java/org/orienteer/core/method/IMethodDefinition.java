package org.orienteer.core.method;

/**
 * 
 * Precise method definition with filters and other things 
 *
 */
public interface IMethodDefinition {
	/**
	 * Returning method id. Should be unique.
	 * @return
	 */
	public String getMethodId();
	/**
	 * Return method instance by input data, using {@link IMethod.initialize} for init 
	 * @param dataObject
	 * @return
	 */
	public IMethod getMethod(IMethodEnvironmentData dataObject);
	/**
	 * Get method order in methods list
	 * @return
	 */
	public int getOrder();
	/**
	 * Check method for using in this environment by method filters
	 * Calls before {@link IMethodDefinition.getMethod} 
	 * @param dataObject
	 * @return
	 */
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject);

}
