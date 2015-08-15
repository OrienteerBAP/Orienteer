package org.orienteer.core.component;

import org.apache.wicket.IGenericComponent;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.command.Command;

/**
 * Interface for components which support {@link Command}s
 *
 * @param <T> the type of main object for a command
 */
public interface ICommandsSupportComponent<T> {
	public ICommandsSupportComponent<T> addCommand(Command<T> command);
	public ICommandsSupportComponent<T> removeCommand(Command<T> command);
	public String newCommandId();
}
