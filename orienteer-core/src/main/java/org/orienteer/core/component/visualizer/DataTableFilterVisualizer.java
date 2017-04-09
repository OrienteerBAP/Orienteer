package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.property.BooleanFilterPanel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.NumberEditPanel;
import org.orienteer.core.component.property.StringEditPanel;
import org.orienteer.core.component.table.filter.DataFilter;

/**
 * @author Vitaliy Gonchar
 */
public class DataTableFilterVisualizer extends AbstractSimpleVisualizer {

    public DataTableFilterVisualizer() {
        super(DataFilter.PROPERTY.getName(), false, OType.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel,
                                         IModel<OProperty> propertyModel, IModel<V> valueModel) {
        Component component = null;
        OType type = propertyModel.getObject().getType();
        switch (type) {
            case STRING:
                component = new StringEditPanel(id, (IModel<String>) valueModel);
                break;
            case BOOLEAN:
                component = new BooleanFilterPanel(id, (IModel<Boolean>) valueModel);
                break;
            case LONG:
            case FLOAT:
            case DECIMAL:
            case INTEGER:
                component = new NumberEditPanel(id, (IModel<Number>) valueModel);
                break;
        }
        if (component != null) {
            component.setOutputMarkupPlaceholderTag(true);
        }
        return component != null ? component : new Label(id, Model.of("Without visualization"));
    }
}
