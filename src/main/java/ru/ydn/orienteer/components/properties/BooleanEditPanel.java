package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

@Deprecated
public class BooleanEditPanel extends GenericPanel<Boolean>
{

	public BooleanEditPanel(String id, IModel<Boolean> model) {
		super(id, model);
		initialize();
	}

	public BooleanEditPanel(String id) {
		super(id);
		initialize();
	}
	
	protected void initialize()
	{
		add(new CheckBox("checkbox", getModel()));
	}
}
