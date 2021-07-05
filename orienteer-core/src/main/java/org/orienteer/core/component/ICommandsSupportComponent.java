package org.orienteer.core.component;

import java.lang.reflect.Type;

import org.apache.wicket.Component;
import org.orienteer.core.component.command.Command;

import com.google.common.reflect.TypeToken;

/**
 * Interface for components which support {@link Command}s
 *
 * @param <T> the type of main object for a command
 */
public interface ICommandsSupportComponent<T> {
	public ICommandsSupportComponent<T> addCommand(Command<T> command);
	public ICommandsSupportComponent<T> removeCommand(Command<T> command);
	public String newCommandId();
	public TypeToken<T> getTypeToken();
	
	public default Component getComponent() {
		return (Component) this;
	}
}
