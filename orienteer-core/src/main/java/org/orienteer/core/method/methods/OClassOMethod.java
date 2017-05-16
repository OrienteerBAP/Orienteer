package org.orienteer.core.method.methods;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.AjaxCommand;

/**
 * 
 * OMethod for display and use OClass methods as buttons in single view
 *
 */
public class OClassOMethod extends AbstractOClassOMethod{

	private Component displayComponent;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			IModel<Object> model = (IModel<Object>) envData.getDisplayObjectModel();
			displayComponent = new AjaxCommand<Object>(id, getTitleModel(),model) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onInitialize() {
					super.onInitialize();
					setIcon(annotation.icon());
					setBootstrapType(annotation.bootstrap());
					setChangingDisplayMode(annotation.changingDisplayMode());	
					setChandingModel(annotation.changingModel());
				}
				@Override
				public void onClick(AjaxRequestTarget target) {
					invoke();
				}
			};
			if (annotation.behaviors().length>0){
				for ( Class<? extends Behavior> behavior : annotation.behaviors()) {
					try {
						displayComponent.add(behavior.newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return displayComponent;
	}
}
