package org.orienteer.core.component.property.date;

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
        get(HOURS).setVisibilityAllowed(false);
        get(MINUTES).setVisibilityAllowed(false);
        get(AM_OR_PM_CHOICE).setVisibilityAllowed(false);
    }

    @Override
    public String getHoursMarkupId() {
        return null;
    }

    @Override
    public String getMinutesMarkupId() {
        return null;
    }
}
