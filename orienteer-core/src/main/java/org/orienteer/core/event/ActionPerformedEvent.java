package org.orienteer.core.event;

import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.component.command.Command;

import com.google.common.reflect.TypeToken;

/**
 * Event to be submitted by {@link Command}s when some action performed
 *
 * @param <T> type of a subject
 */
public class ActionPerformedEvent<T> {
	
	private final T object;
	private final Command<T> command;
	private AjaxRequestTarget target;
	private boolean ajaxChecked=false;
	
	public ActionPerformedEvent(Command<T> command) {
		this(command.getModelObject(), command);
	}
	
	public ActionPerformedEvent(T object, Command<T> command) {
		this(object, command, null);
	}
	
	public ActionPerformedEvent(T object, Command<T> command, AjaxRequestTarget target) {
		this.object = object;
		this.command = command;
		this.target = target;
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

	public AjaxRequestTarget getTarget() {
		if(target==null && !ajaxChecked) {
			target = RequestCycle.get().find(AjaxRequestTarget.class);
			ajaxChecked=true;
		}
		return target;
	}
	
	public boolean isAjax() {
		return getTarget()!=null;
	}
	
	public boolean ofType(Class<?> type) {
		return object!=null && type!=null && type.isInstance(object);
	}
	
}
