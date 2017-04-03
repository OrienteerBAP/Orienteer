package org.orienteer.core.component.table.filter;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * @author Vitaliy Gonchar
 */
public interface IFilterSupportComponent {
    void setFilter(IDataFilter<?> dataFilter, IModel<DisplayMode> modeModel);
}
