package org.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.components.OIndexPageLink;
import org.orienteer.components.OPropertyPageLink;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OIndexViewPanel extends AbstractLinkViewPanel<OIndex<?>> {

	/**
	 * 
	 */
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