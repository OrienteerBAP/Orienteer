package org.orienteer.core.method;

import org.apache.wicket.Component;

/**
 * 
 * Base interface for all methods
 * If you need to use source method definition, annotate you class by {@link OMethod} annotation}
 *
 */
public interface IMethod {
	/**
	 * Init method instance by environment data and individual config
	 * Called only once per creation in linked {@link IMethodDefinition}
	 * 
	 * @param envData
	 */
	public void methodInit(String id,IMethodEnvironmentData envData,IMethodConfig config);
	
	/**
	 * Return display {@link Component} with assigned id
	 * May be created every time or storages into method 
	 * If you need to use integrated markup, see {@link ExampleMethodWithIntMarkup} for example
	 * 
	 * @return
	 */
	public Component getDisplayComponent();
}
