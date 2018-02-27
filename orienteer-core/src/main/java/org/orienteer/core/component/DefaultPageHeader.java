package org.orienteer.core.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.IDashboard;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * Default simple page header. Displays just provided title
 */
public class DefaultPageHeader extends GenericPanel<String> {
	private static final long serialVersionUID = 1L;
	private DefaultPageHeaderMenu menu;

	public DefaultPageHeader(String id, IModel<String> model) {
		super(id, model);
		add(new Label("label", model));
		
		add(menu = new DefaultPageHeaderMenu("menu"));
	}
}
