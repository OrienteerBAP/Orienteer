package org.orienteer.core.component.visualizer;

import java.util.Collection;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

import org.orienteer.core.util.ODocumentChoiceProvider;
import org.wicketstuff.select2.*;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IVisualizer} to show links as tags control
 */
public class TagsVisualizer extends AbstractSimpleVisualizer {
    public TagsVisualizer() {
        super("suggest", false, OType.LINK, OType.LINKLIST, OType.LINKSET);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Component createComponent(String id, DisplayMode mode,
                                         IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
        if (DisplayMode.EDIT.equals(mode)) {
            OProperty property = propertyModel.getObject();
            OClass oClass = property.getLinkedClass();
            if (property.getType().isMultiValue()) {
                return new Select2MultiChoice<ODocument>(id, (IModel<Collection<ODocument>>) valueModel, new ODocumentChoiceProvider(oClass));
            } else {
                return new Select2Choice<ODocument>(id, (IModel<ODocument>) valueModel, new ODocumentChoiceProvider(oClass));
            }
        } else {
            return null;
        }
    }

}