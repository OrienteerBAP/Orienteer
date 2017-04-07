package org.orienteer.core.component.table.filter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * @author Vitaliy Gonchar
 */
public interface IFilterSupportComponent {
    void setFilter(IDataFilter<?> dataFilter, IModel<OClass> classModel, IModel<DisplayMode> modeModel);
}
