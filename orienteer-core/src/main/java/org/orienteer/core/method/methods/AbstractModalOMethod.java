package org.orienteer.core.method.methods;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
/**
 * 
 *  Modal windows support for OMethod
 *
 */
public abstract class AbstractModalOMethod extends AbstractOMethod{
	private static final long serialVersionUID = 1L;
	
	private AbstractModalWindowCommand<Object> displayComponent;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			IModel<Object> model = (IModel<Object>) getEnvData().getDisplayObjectModel();

			displayComponent = new AbstractModalWindowCommand<Object>(getId(), getTitleModel(),model) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onInitialize() {
					super.onInitialize();
					applyVisualSettings(this);
				}
				@Override
				protected void initializeContent(ModalWindow modal) {
					modal.setTitle(getTitleModel());
					modal.setContent(getModalContent(modal.getContentId(),modal,this));		
				}
				@Override
				public void onAfterModalSubmit() {
					sendActionPerformed();					
				}
			};
			applyBehaviors(displayComponent);
		}
		return displayComponent;
	}

	public abstract Component getModalContent(String componentId,ModalWindow modal,AbstractModalWindowCommand<?> command);
}
