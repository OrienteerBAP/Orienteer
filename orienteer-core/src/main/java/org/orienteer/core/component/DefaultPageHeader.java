package org.orienteer.core.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * Default simple page header. Displays just provided title
 */
public class DefaultPageHeader extends GenericPanel<String> {

	public DefaultPageHeader(String id, IModel<String> model) {
		super(id, model);
		add(new Label("label", model));
	}

}
