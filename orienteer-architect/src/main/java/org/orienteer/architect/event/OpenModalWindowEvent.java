package org.orienteer.architect.event;

import org.apache.http.util.Args;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.function.Function;

/**
 * Event which represents open modal window
 */
public class OpenModalWindowEvent extends AbstractModalWindowEvent {

    private final IModel<String> titleModel;
    private final Function<String, Component> contentGenerator;

    public OpenModalWindowEvent(AjaxRequestTarget target,
                                IModel<String> titleModel,
                                SerializableFunction<String, Component> contentGenerator
    ) {
        super(target);
        Args.notNull(titleModel, "titleModel");
        Args.notNull(contentGenerator, "contentGenerator");
        this.titleModel = titleModel;
        this.contentGenerator = contentGenerator;
    }

    @Override
    public void execute(ModalWindow modalWindow) {
        modalWindow.setContent(contentGenerator.apply(modalWindow.getContentId()));
        modalWindow.setTitle(titleModel);
        modalWindow.show(target);
    }
}
