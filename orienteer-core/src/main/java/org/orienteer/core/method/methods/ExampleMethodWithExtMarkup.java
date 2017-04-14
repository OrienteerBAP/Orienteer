package org.orienteer.core.method.methods;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.method.Filter;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.Method;
import org.orienteer.core.method.filters.DisallowFilter;

/**
 * 
 * Example method with external markup.
 * More flexible than internal markup.
 * Using for complex methods.
 */

@Method(order=2,filters={
		@Filter(fClass = DisallowFilter.class, fData = ""), // not need to show this method outside development
		//@Filter(fClass = WidgetTypeFilter.class, fData = "parameters|list-all"),
		//@Filter(fClass = OEntityFilter.class, fData = "OUser")
		//@Filter(fClass = PlaceFilter.class, fData = "ACTIONS|DATA_TABLE|STRUCTURE_TABLE"),
})
public class ExampleMethodWithExtMarkup implements Serializable,IMethod{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Component displayComponent;

	@Override
	public void initialize(IMethodEnvironmentData envData) {
		//TODO: stub
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		if (displayComponent==null){
			displayComponent = new AjaxCommand<Object>(componentId, "command.settings") {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
				}
				
				@Override
				protected void onConfigure() {
					super.onConfigure();
				}
			};
		}else{
			displayComponent.setMarkupId(componentId);
		}
		return displayComponent;
	}



}
