package org.orienteer.core.method;

/**
 * 
 * Precise method definition with filters and other things 
 *
 */
public interface IMethodDefinition {
	IMethod getMethod(IMethodEnvironmentData dataObject);
	boolean isSupportedMethod(IMethodEnvironmentData dataObject);

}
