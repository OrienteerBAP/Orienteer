package org.orienteer.core.method.methods;

import org.apache.wicket.Component;

import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.core.method.Filter;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.Method;
import org.orienteer.core.method.filters.DisallowFilter;

/**
 * Example method with internal markup.
 * Less flexible than internal markup.
 * Using for simple methods.
 * 
 */

@Method(order=10,filters={
		@Filter(fClass = DisallowFilter.class, fData = ""), // not need to show this method outside development
		//@Filter(fClass = WidgetTypeFilter.class, fData = "parameters"),
		//@Filter(fClass = DisplayModeFilter.class, fData = "EDIT"),
		//@Filter(fClass = OEntityFilter.class, fData = "OUser")
		//@Filter(fClass = PlaceFilter.class, fData = "ACTIONS|DATA_TABLE|STRUCTURE_TABLE")
})
public class ExampleMethodWithIntMarkup extends Panel implements IMethod{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExampleMethodWithIntMarkup() {
		//Wicket components should be own unique id`s  
		super("method"+Math.random());
	}
	
	@Override
	public void initialize(IMethodEnvironmentData envData) {
		//TODO: stub
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		setMarkupId(componentId);
		return this;
	}



}
