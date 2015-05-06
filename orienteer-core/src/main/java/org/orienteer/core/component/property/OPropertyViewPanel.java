package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.OPropertyPageLink;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link Panel} to view a link to an {@link OProperty}
 */
public class OPropertyViewPanel extends AbstractLinkViewPanel<OProperty> {

	private static final long serialVersionUID = 1L;

	public OPropertyViewPanel(String id, IModel<OProperty> model) {
		super(id, model);
	}

	public OPropertyViewPanel(String id) {
		super(id);
	}
	
	@Override
	protected AbstractLink newLink(String id) {
		return new OPropertyPageLink(id, getModel()).setPropertyNameAsBody(true);
	}

}
