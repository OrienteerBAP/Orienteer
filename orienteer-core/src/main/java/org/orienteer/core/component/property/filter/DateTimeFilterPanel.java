package org.orienteer.core.component.property.filter;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.model.IModel;

import java.util.Date;

/**
 * @author Vitaliy Gonchar
 */
public class DateTimeFilterPanel extends AbstractFilterUpdaterPanel<Date> {
    public DateTimeFilterPanel(String id, IModel<Date> valueModel) {
        super(id, valueModel);
    }

    @Override
    protected Component getFilterComponent(IModel<Date> valueModel) {
        return add(new DateTimeField("date", valueModel));
    }
}
