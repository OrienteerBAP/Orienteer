package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.OLocalizationEditPanel;
import org.orienteer.core.util.LocalizeFunction;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.FunctionModel;

/**
 * {@link IVisualizer} to display and modify documents localizations in Orienteer.
 */
public class LocalizationVisualizer extends AbstractSimpleVisualizer {

    public static final String NAME = "localization";

    public LocalizationVisualizer() {
        super(NAME, false, OType.EMBEDDEDMAP);
    }

    @Override
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        switch (mode)
        {
            case VIEW:
            	return new Label(id, new FunctionModel<>(new DynamicPropertyValueModel<>(documentModel, propertyModel), LocalizeFunction.getInstance()));
            case EDIT:
                return new OLocalizationEditPanel<V>(id, documentModel, propertyModel).setType(String.class);
            default:
                return null;
        }
    }
}
