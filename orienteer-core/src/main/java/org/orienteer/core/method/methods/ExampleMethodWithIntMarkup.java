package org.orienteer.core.method.methods;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.Method;


@Method(order=10,filters={
		//@Filter(fClass = WidgetTypeFilter.class, fData = "parameters")
})
public class ExampleMethodWithIntMarkup extends Panel implements IMethod{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ExampleMethodWithIntMarkup() {
		super("method"+Math.random());
	}
	
	@Override
	public void initialize(IMethodEnvironmentData envData) {
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		setMarkupId(componentId);
		return this;
	}



}
