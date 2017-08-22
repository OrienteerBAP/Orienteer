package org.orienteer.core.widget.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.widget.AbstractWidget;

/**
 * Command to make widget fullscreen and back 
 * @param <T> the type of an entity to which this command can be applied
 */
public class FullScreenCommand<T> extends AjaxCommand<T> {
	
	private boolean expanded = false;
	public FullScreenCommand(String commandId) {
		super(commandId, Model.of());
		setAutoNotify(false);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		getLink().get("label").setDefaultModelObject(getLocalizer().getString(expanded?"command.fullscreen.min":"command.fullscreen.max", null));
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		expanded=!expanded;
		configure();
		target.add(this);
		AbstractWidget<?> widget = findParent(AbstractWidget.class);
		target.appendJavaScript("$('body').toggleClass('noscroll'); $('#"+widget.getMarkupId()+"').toggleClass('fullscreen');");
	}

}
