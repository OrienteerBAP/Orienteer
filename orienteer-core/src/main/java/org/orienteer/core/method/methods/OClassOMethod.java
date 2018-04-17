package org.orienteer.core.method.methods;

import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;

/**
 * 
 * OMethod for display and use OClass methods as buttons in single view
 *
 */
public class OClassOMethod extends AbstractOMethod{

	private Command<?> displayComponent;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Command<?> createCommand(String id) {
		if (displayComponent==null){
			IModel<Object> model = (IModel<Object>) getContext().getDisplayObjectModel();
			displayComponent = new AjaxCommand<Object>(id, getTitleModel(),model) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onInitialize() {
					super.onInitialize();
					applyVisualSettings(this);
				}
				@Override
				public void onClick(Optional<AjaxRequestTarget> target) {
					invoke();
				}
			};
			applyBehaviors(displayComponent);
		}
		return displayComponent;
	}
}
