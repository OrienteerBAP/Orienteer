package org.orienteer.core.component.visualizer;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IVisualizer} to work with binary in hex representation
 */
public class HexVisualizer extends AbstractSimpleVisualizer {

	public static final String NAME = "hex";
    public HexVisualizer()
    {
        super(NAME,false, OType.BINARY);
    }
	@Override
	public <V> Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel, IModel<V> valueModel) {
		IModel<byte[]> model = (IModel<byte[]>)valueModel;
		switch (mode)
        {
            case VIEW:
                return new MultiLineLabel(id, valueModel);
            case EDIT:
                return new TextArea<>(id, valueModel).setType(byte[].class);
            default:
                return null;
        }
	}

}
