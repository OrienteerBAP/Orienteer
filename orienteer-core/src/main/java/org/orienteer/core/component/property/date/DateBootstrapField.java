package org.orienteer.core.component.property.date;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * Bootstrap enabled date field
 */
public class DateBootstrapField extends DateTimeBootstrapField {

    public DateBootstrapField(String id) {
        super(id);
    }

    public DateBootstrapField(String id, IModel<Date> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        get(HOURS).setVisible(false);
        get(MINUTES).setVisible(false);
        get(AM_OR_PM_CHOICE).setVisible(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void convertTimeInput() {
        setConvertedInput(((TextField<Date>)get(DATE)).getModelObject());
    }

    @Override
    protected WebMarkupContainer createTimeSeparator(String id) {
        WebMarkupContainer container = super.createTimeSeparator(id);
        container.setVisible(false);
        return container;
    }
}
