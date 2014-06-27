package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.OClassPageLink;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassViewPanel extends GenericPanel<OClass> {

	public OClassViewPanel(String id, IModel<OClass> model) {
		super(id, model);
		initialize();
	}

	public OClassViewPanel(String id) {
		super(id);
		initialize();
	}
	
	protected void initialize()
	{
		add(new OClassPageLink("link", getModel()).setClassNameAsBody(true));
	}

}
