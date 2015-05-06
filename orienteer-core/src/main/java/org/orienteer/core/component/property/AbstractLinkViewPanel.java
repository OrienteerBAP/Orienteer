package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * {@link GenericPanel} for displaying of {@link Link}s
 *
 * @param <T>
 */
public abstract class AbstractLinkViewPanel<T> extends GenericPanel<T> {

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
