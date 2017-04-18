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
	 * Init method instance by environment data
	 * Called only once per creation 
	 * 
	 * @param envData
	 */
	public void methodInit(String id,IMethodEnvironmentData envData);
	
	/**
	 * Return display {@link Component} with signed id
	 * May be created every time or storages into method 
	 * If you need to use integrated markup, see {@link ExampleMethodWithIntMarkup} for example
	 * 
	 * @return
	 */
	public Component getDisplayComponent();
}
