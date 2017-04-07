package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * @author Vitaliy Gonchar
 */
public class StringEditPanel extends GenericPanel<String> {
    public StringEditPanel(String id, IModel<String> value) {
        super(id, value);
        add(new TextField<String>("stringEdit", value));
    }


}
