package org.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public abstract class AbstractLinkViewPanel<T> extends GenericPanel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



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
