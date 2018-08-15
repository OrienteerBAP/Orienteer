package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.orienteer.core.component.command.Command;

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
	 * @param envData
	 */
	public void init(IMethodDefinition config,IMethodContext context);
	
	/**
	 * Return display {@link Command} with assigned id
	 * May be created every time or storages into method 
	 * If you need to use integrated markup, see {@link org.orienteer.core.method.ExampleMethodWithIntMarkup} for example
	 * @param id identification of the {@link Command} to be created 
	 * 
	 * @return created {@link Command}
	 */
	public Command<?> createCommand(String id);
}
