package ru.ydn.orienteer.components.properties;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class BooleanEditPanel extends GenericPanel<Boolean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		add(newCheckbox("checkbox"));
	}
	
	protected Component newCheckbox(String componentId)
	{
		return new CheckBox(componentId, getModel());
	}
}
