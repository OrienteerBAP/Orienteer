package org.orienteer.core.event;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.command.Command;

/**
 * Event to be submitted by {@link Command}s when some action performed
 *
 * @param <T> type of a subject
 */
public class ActionPerformedEvent<T> {
	
	private final T object;
	private final Command<T> command;
	private Optional<AjaxRequestTarget> targetOptional;
	
	public ActionPerformedEvent(Command<T> command) {
		this(command.getModelObject(), command);
	}
	
	public ActionPerformedEvent(T object, Command<T> command) {
		this(object, command, null);
	}
	
	public ActionPerformedEvent(T object, Command<T> command, Optional<AjaxRequestTarget> targetOptional) {
		this.object = object;
		this.command = command;
		this.targetOptional = targetOptional;
	}

	public Command<T> getCommand() {
		return command;
	} 
	
	public boolean isCommandInstanceOf(Class<?> clazz) {
		return command!=null && clazz.isInstance(command);
	}

	public T getObject() {
		return object;
	}

	public Optional<AjaxRequestTarget> getTarget() {
		if(targetOptional==null) {
			targetOptional = RequestCycle.get().find(AjaxRequestTarget.class);
		}
		return targetOptional;
	}
	
	public boolean isAjax() {
		return getTarget()!=null;
	}
	
	public boolean ofType(Class<?> type) {
		return object!=null && type!=null && type.isInstance(object);
	}
	
}
