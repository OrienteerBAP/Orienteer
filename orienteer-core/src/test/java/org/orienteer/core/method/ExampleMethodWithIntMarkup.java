package org.orienteer.core.method;

import org.apache.wicket.Component;

import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.DisallowFilter;

/**
 * Example method with internal markup.
 * Less flexible than external markup.
 * Using for simple methods.
 * 
 */

@OMethod(order=10,filters={
		@OFilter(fClass = DisallowFilter.class, fData = ""), // not need to show this method outside development
		//@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),
		//@OFilter(fClass = OEntityFilter.class, fData = "OUser")
		//@OFilter(fClass = PlaceFilter.class, fData = "DATA_TABLE|STRUCTURE_TABLE")
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
	public void methodInit(String id, IMethodContext envData,IMethodDefinition config) {
		setMarkupId(id);
	}

	@Override
	public Component getDisplayComponent() {
		return this;
	}



}
