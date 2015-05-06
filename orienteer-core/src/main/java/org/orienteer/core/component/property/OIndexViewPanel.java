package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.OIndexPageLink;
import org.orienteer.core.component.OPropertyPageLink;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link Panel} to view a link to an {@link OIndex}
 */
public class OIndexViewPanel extends AbstractLinkViewPanel<OIndex<?>> {

	private static final long serialVersionUID = 1L;

	public OIndexViewPanel(String id, IModel<OIndex<?>> model) {
		super(id, model);
	}

	public OIndexViewPanel(String id) {
		super(id);
	}
	
	@Override
	protected AbstractLink newLink(String id) {
		return new OIndexPageLink(id, getModel()).setPropertyNameAsBody(true);
	}

}