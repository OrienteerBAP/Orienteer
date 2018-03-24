package org.orienteer.core.widget.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.widget.AbstractWidget;

import java.util.Optional;

/**
 * Command to make widget fullscreen and back 
 * @param <T> the type of an entity to which this command can be applied
 */
public class FullScreenCommand<T> extends AjaxCommand<T> {
	private static final long serialVersionUID = 1L;
	private boolean expanded = false;

	public FullScreenCommand(String commandId) {
		super(commandId, Model.of());
		setAutoNotify(false);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		expanded = false;
		updateLabel();
	}

	@Override
	public void onClick(Optional<AjaxRequestTarget> targetOptional) {
		expanded=!expanded;
		updateLabel();
		if(targetOptional.isPresent()) {
			AjaxRequestTarget target = targetOptional.get();
			target.add(getLink());
			AbstractWidget<?> widget = findParent(AbstractWidget.class);
			target.appendJavaScript("$('body').toggleClass('noscroll'); $('#"+widget.getMarkupId()+"').toggleClass('fullscreen');");
		}
	}

	private void updateLabel() {
		getLink().get("label").setDefaultModelObject(getLocalizer().getString(expanded?"command.fullscreen.min":"command.fullscreen.max", null));
	}
}
