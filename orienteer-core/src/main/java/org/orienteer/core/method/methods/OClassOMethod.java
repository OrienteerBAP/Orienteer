package org.orienteer.core.method.methods;

import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.AjaxCommand;

/**
 * 
 * OMethod for display and use OClass methods as buttons in single view
 *
 */
public class OClassOMethod extends AbstractOMethod{

	private Component displayComponent;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			IModel<Object> model = (IModel<Object>) getMethodContext().getDisplayObjectModel();
			displayComponent = new AjaxCommand<Object>(getId(), getTitleModel(),model) {
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
