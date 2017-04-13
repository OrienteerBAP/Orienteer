package org.orienteer.core.component.property.filter;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.model.IModel;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author Vitaliy Gonchar
 */
public class DateFilterPanel extends AbstractFilterUpdaterPanel<Date> {

    public DateFilterPanel(String id, IModel<Date> valueModel) {
        super(id, valueModel);

    }

    @Override
    protected Component getFilterComponent(IModel<Date> valueModel) {
        return add(new DateField("date", valueModel) {
            @Override
            protected TimeZone getClientTimeZone() {
                // We should not convert timezones when working with just dates.
                return null;
            }
        });
    }
}
