package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * {@link GenericPanel} for displaying of {@link Link}s
 *
 * @param <T> the type of the panel's model object
 */
public abstract class AbstractLinkViewPanel<T> extends GenericPanel<T> {

	private static final long serialVersionUID = 1L;
	protected AbstractLink link;


	public AbstractLinkViewPanel(
			String id,
			IModel<T> valueModel) {
		super(id, valueModel);
	}
	
	public AbstractLinkViewPanel(String id)
	{
		super(id);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(link = newLink("link"));
	}

	protected abstract AbstractLink newLink(String id);

}
