package org.orienteer.architect.component.panel.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.orienteer.core.widget.command.FullScreenCommand;

/**
 * Command for enable fullscreen for  {@link org.orienteer.architect.component.widget.OArchitectEditorWidget}
 */
public class OArchitectFullscreenCommand extends FullScreenCommand<Void> {

    private boolean clickOnF11 = false;

    public OArchitectFullscreenCommand(String commandId) {
        super(commandId);
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        super.onClick(target);
        if (!clickOnF11)
            target.appendJavaScript("app.switchFullScreenMode(true);");
    }

    public void setClickOnF11(boolean clickOnF11) {
        this.clickOnF11 = clickOnF11;
    }
}
