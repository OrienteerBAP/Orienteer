package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.filter.BooleanFilterPanel;
import org.orienteer.core.component.property.filter.DateFilterPanel;
import org.orienteer.core.component.property.filter.DateTimeFilterPanel;
import org.orienteer.core.component.property.filter.TextEditFilterPanel;
import org.orienteer.core.component.table.filter.DataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Vitaliy Gonchar
 */
public class DataTableFilterVisualizer extends AbstractSimpleVisualizer {

    public DataTableFilterVisualizer() {
        super(DataFilter.PROPERTY.getName(), false, OType.values());
    }

    private static final Logger LOG = LoggerFactory.getLogger(DataTableFilterVisualizer.class);

    @Override
    @SuppressWarnings("unchecked")
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel,
                                         IModel<OProperty> propertyModel, final IModel<V> valueModel) {
        Component component;
        OType type = propertyModel.getObject().getType();
        switch (type) {
            case STRING:
                component = new TextEditFilterPanel<>(id, (IModel<String>) valueModel);
                break;
            case BOOLEAN:
                component = new BooleanFilterPanel(id, (IModel<Boolean>) valueModel);
                break;
            case INTEGER:
            case SHORT:
            case BYTE:
            case LONG:
            case DECIMAL:
            case FLOAT:
            case DOUBLE:
                component = new TextEditFilterPanel<>(id, (IModel<Number>) valueModel);
                break;
            case DATETIME:
                component = new DateTimeFilterPanel(id, (IModel<Date>) valueModel);
                break;
            case DATE:
                component = new DateFilterPanel(id, (IModel<Date>) valueModel);
                break;
            default:
                component = new Label(id, Model.of("Without visualization"));
        }

        component.setOutputMarkupPlaceholderTag(true);

        return component;
    }
}
