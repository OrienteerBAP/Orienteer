package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * @author Vitaliy Gonchar
 */
public class NumberEditPanel extends GenericPanel<Number> {
    public NumberEditPanel(String id, IModel<Number> model) {
        super(id, model);
        add(new TextField<Number>("numberEdit", model));
    }
}
