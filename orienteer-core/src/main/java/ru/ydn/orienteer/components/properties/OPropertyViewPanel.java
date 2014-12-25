package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.OPropertyPageLink;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyViewPanel extends AbstractLinkViewPanel<OProperty> {

	/**
	 * 
	 */
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
