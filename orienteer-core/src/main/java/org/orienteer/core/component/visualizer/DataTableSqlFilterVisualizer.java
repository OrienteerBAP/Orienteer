package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.filter.SqlFilterPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.filter.DataFilter;

/**
 * @author Vitaliy Gonchar
 */
public class DataTableSqlFilterVisualizer extends AbstractSimpleVisualizer {
    public DataTableSqlFilterVisualizer() {
        super(DataFilter.SQL.getName(), false, OType.STRING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Component createComponent(String id, final DisplayMode mode, IModel<ODocument> documentModel,
                                         IModel<OProperty> propertyModel, final IModel<V> valueModel) {
        Component component = new SqlFilterPanel(id, (IModel<String>) valueModel, mode.asModel());

        return component;
    }
}
