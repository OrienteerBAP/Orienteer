package org.orienteer.architect.event;

import org.apache.http.util.Args;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import java.io.Serializable;

/**
 * Abstract event for modal window
 */
public abstract class AbstractModalWindowEvent implements Serializable {

    protected final AjaxRequestTarget target;

    public AbstractModalWindowEvent(AjaxRequestTarget target) {
        Args.notNull(target, "target");
        this.target = target;
    }

    public abstract void execute(ModalWindow modalWindow);
}
