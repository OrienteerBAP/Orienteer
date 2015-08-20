package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.property.ImageEditPanel;
import org.orienteer.core.component.property.ImageViewPanel;

/**
 * {@link IVisualizer} to display binary images.
 */
public class ImageVisualizer extends AbstractSimpleVisualizer {
    public ImageVisualizer() {
        super("image", false, OType.BINARY);
    }

    @Override
    public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        switch (mode)
        {
            case VIEW:
                return new ImageViewPanel<V>(id, valueModel);
            case EDIT:
                return new ImageEditPanel(id, (IModel<byte[]>)valueModel);
            default:
                return null;
        }
    }
}
