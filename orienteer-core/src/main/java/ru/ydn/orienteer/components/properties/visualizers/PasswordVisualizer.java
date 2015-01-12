package ru.ydn.orienteer.components.properties.visualizers;

import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.PasswordsPanel;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class PasswordVisualizer extends AbstractSimpleVisualizer
{
	public static final String NAME = "password";
	public PasswordVisualizer()
	{
		super(NAME, false, OType.STRING);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> Component createComponent(String id, DisplayMode mode,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel, IModel<V> valueModel) {
		if(mode==DisplayMode.VIEW)
		{
			return new Label(id, "*****");
		}
		else if(mode==DisplayMode.EDIT)
		{
			return new PasswordsPanel(id, (IModel<String>)valueModel);
		}
		else
		{
			return null;
		}
	}

}
