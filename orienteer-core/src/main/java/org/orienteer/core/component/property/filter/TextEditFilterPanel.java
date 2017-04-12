package org.orienteer.core.component.property.filter;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * @author Vitaliy Gonchar
 * @param <T> - type of value
 */
public class TextEditFilterPanel<T> extends AbstractFilterUpdaterPanel<T> {

    public TextEditFilterPanel(String id, final IModel<T> valueModel) {
        super(id, valueModel);
    }

    @Override
    protected Component getFilterComponent(IModel<T> valueModel) {
        return add(new TextField<>("stringEdit", valueModel));
    }
}
