package org.orienteer.architect.component.panel.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.command.FullScreenCommand;

/**
 * Command which enable fullscreen mode for 'orienteer-architect'
 * @param <T> type
 */
public class OArchitectFullScreenCommand<T> extends FullScreenCommand<T> {

    public OArchitectFullScreenCommand(String commandId) {
        super(commandId);
    }

    public void setExpanded(AjaxRequestTarget target, boolean expanded) {
        this.expanded = expanded;
        configure();
        target.add(this);
    }

    @Override
    protected void appendJavaScript(AbstractWidget<?> widget, AjaxRequestTarget target) {
        target.appendJavaScript(String.format("; app.switchFullScreenMode('%s');", widget.getMarkupId()));
    }
}
