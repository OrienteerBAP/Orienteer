package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * 
 * Base interface for all methods
 * If you need to use source method definition, annotate you class by {@link Method} annotation}
 *
 */
public interface IMethod {
	/**
	 * Init method instance by environment data
	 * Called only once per creation 
	 * 
	 * @param envData
	 */
	public void initialize(IMethodEnvironmentData envData);
	
	/**
	 * Return display {@link Component} with signed id
	 * May be created every time or storages into method 
	 * 
	 * @param componentId
	 * @return
	 */
	public Component getDisplayComponent(String componentId);
}
