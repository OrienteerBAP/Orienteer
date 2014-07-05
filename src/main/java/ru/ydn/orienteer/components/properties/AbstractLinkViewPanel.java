package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public abstract class AbstractLinkViewPanel<T> extends GenericPanel<T> {

	public AbstractLinkViewPanel(
			String id,
			IModel<T> valueModel) {
		super(id, valueModel);
		add(newLink("link"));
	}
	
	public AbstractLinkViewPanel(String id)
	{
		super(id);
		add(newLink("link"));
	}



	protected abstract AbstractLink newLink(String id);

}
