package org.orienteer.core.method.methods;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.Command;

/**
 * 
 * Modal windows support
 *
 */
public abstract class AbstractOClassModalOMethod extends AbstractOClassOMethod{
	
	private AbstractModalWindowCommand<Object> displayComponent;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Component getDisplayComponent() {
		if (displayComponent==null){
			IModel<Object> model = (IModel<Object>) envData.getDisplayObjectModel();

			displayComponent = new AbstractModalWindowCommand<Object>(id, getTitleModel(),model) {
				

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
				protected void initializeContent(ModalWindow modal) {
					modal.setTitle(getTitleModel());
					
					modal.setContent(getModalContent(modal.getContentId(),modal,this));		
				}
				@Override
				public void onWindowClose() {
					sendActionPerformed();					
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
	/**
	 * Should call {@link AbstractOClassOMethod.invoke} somewhere inside
	 * @param componentId
	 * @param modal
	 * @return
	 */
	public abstract Component getModalContent(String componentId,ModalWindow modal,AbstractModalWindowCommand<?> command);
}
