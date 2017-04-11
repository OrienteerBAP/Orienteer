package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.Method;
import org.orienteer.core.method.filters.WidgetTypeFilter;

@Method(order=2,filters={
		//@Filter(fClass = WidgetTypeFilter.class, fData = "parameters")
})
public class ExampleMethodWithExtMarkup implements Serializable,IMethod{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Component displayComponent;

	@Override
	public void initialize(IMethodEnvironmentData envData) {
		
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		if (displayComponent==null){
			displayComponent = new Label(componentId,"SimpleMethod2");
		}else{
			displayComponent.setMarkupId(componentId);
		}
		return displayComponent;
	}



}
