package org.orienteer.core.method.methods;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.command.Command;

/**
 * 
 * Orienteer {@link Command} wrapper
 *
 */
public abstract class CommandWrapperMethod extends AbstractOMethod{
	private static final long serialVersionUID = 1L;
	private Command<?> displayComponent;
	
	@Override
	public Command<?> createCommand(String id) {
		if (displayComponent==null){
			displayComponent = getWrappedCommand(id);
			applySettings(displayComponent);
			if (!Strings.isEmpty(getDefinition().getTitleKey())){
				displayComponent.setLabelModel(getTitleModel()); 
			}
			 
		}
		return displayComponent;
	}
	
	public abstract Command<?> getWrappedCommand(String id);
}