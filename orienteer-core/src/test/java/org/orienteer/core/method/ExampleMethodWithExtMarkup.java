package org.orienteer.core.method;

import java.io.Serializable;
import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.IMethodDefinition;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.DisallowFilter;

/**
 * 
 * Example method with external markup.
 * More flexible than internal markup.
 * Using for complex methods.
 */

@OMethod(order=2,filters={
		@OFilter(fClass = DisallowFilter.class, fData = ""), // not need to show this method outside development
		//@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters|list-all"),
		//@OFilter(fClass = OEntityFilter.class, fData = "OUser")
		//@OFilter(fClass = PlaceFilter.class, fData = "ACTIONS|DATA_TABLE|STRUCTURE_TABLE"),
})
public class ExampleMethodWithExtMarkup implements Serializable,IMethod{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Command<?> displayComponent;

	@Override
	public void init(IMethodDefinition config, IMethodContext envData) {
	}

	@Override
	public Command<?> createCommand(String id) {
		if (displayComponent==null){
			displayComponent = new AjaxCommand<Object>(id, "command.settings") {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(Optional<AjaxRequestTarget> target) {
				}
				
				@Override
				protected void onConfigure() {
					super.onConfigure();
				}
			};
		}
		return displayComponent;
	}



}
