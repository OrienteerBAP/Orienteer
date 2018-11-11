package org.orienteer.core.component.visualizer;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.component.editor.HtmlCssJsEditorPanel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * {@link IVisualizer} to display and modify HTML in Orienteer
 */
public class HTMLVisualizer extends AbstractSimpleVisualizer
{
	public static final String NAME = "html";
	public HTMLVisualizer()
	{
		super(NAME, false, OType.STRING);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Component createComponent(String id, DisplayMode mode,
			IModel<ODocument> documentModel, IModel<OProperty> propertyModel,
			IModel<V> valueModel) {
		switch (mode)
		{
			case VIEW:
				return new Label(id, valueModel).setEscapeModelStrings(false);
			case EDIT:
				return new HtmlCssJsEditorPanel(id, (IModel<String>) valueModel, Model.of(mode));
			default:
				return null;
		}
	}

}
