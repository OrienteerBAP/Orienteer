package org.orienteer.architect.event;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.function.Consumer;

/**
 * Event which represents close modal window
 */
public class CloseModalWindowEvent extends AbstractModalWindowEvent {

    private final Consumer<AjaxRequestTarget> callback;

    public CloseModalWindowEvent(AjaxRequestTarget target, SerializableConsumer<AjaxRequestTarget> callback) {
        super(target);
        this.callback = callback;
    }

    @Override
    public void execute(ModalWindow modalWindow) {
        modalWindow.setWindowClosedCallback(callback::accept);
        modalWindow.close(target);
    }
}
