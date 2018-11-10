package org.orienteer.core.component.visualizer;

import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.PatternValidator;
import org.orienteer.core.component.property.DisplayMode;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.FunctionModel;

/**
 * {@link IVisualizer} to work with binary in hex representation
 */
public class HexVisualizer extends AbstractSimpleVisualizer {

	private static final String NAME = "hex";
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
