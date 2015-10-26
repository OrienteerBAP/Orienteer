package org.orienteer.core.component.property;

import com.orientechnologies.orient.core.storage.OCluster;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.OClusterPageLink;

/**
 * {@link Panel} to view a link to a {@link OCluster}
 */
public class OClusterViewPanel extends AbstractLinkViewPanel<OCluster> {

	private static final long serialVersionUID = 1L;

	public OClusterViewPanel(String id, IModel<OCluster> model) {
		super(id, model);
	}

	public OClusterViewPanel(String id) {
		super(id);
	}
	
	@Override
	protected AbstractLink newLink(String id) {
        return new OClusterPageLink("link", getModel()).setPropertyNameAsBody(true);
	}

}
