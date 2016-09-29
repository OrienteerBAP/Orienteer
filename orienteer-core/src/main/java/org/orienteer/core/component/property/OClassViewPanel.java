package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.OClassPageLink;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link Panel} to view a link to a {@link OClass}
 */
public class OClassViewPanel extends AbstractLinkViewPanel<OClass> {
	private static final long serialVersionUID = 1L;
	
	private boolean localizeClassName; 

	public OClassViewPanel(String id, IModel<OClass> model) {
		this(id, model, false);
	}
	
	public OClassViewPanel(String id, IModel<OClass> model, boolean localizeClassName) {
		super(id, model);
		this.localizeClassName = localizeClassName;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		((OClassPageLink)link).setClassNameAsBody(localizeClassName);
	}
	
	@Override
	protected OClassPageLink newLink(String id) {
		return new OClassPageLink("link", getModel());
	}

}
