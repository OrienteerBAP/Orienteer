package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.ODocumentPageLink;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinkViewPanel extends AbstractLinkViewPanel<ODocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LinkViewPanel(
			String id,
			IModel<ODocument> valueModel) {
		super(id, valueModel);
	}
	
	@Override
	protected AbstractLink newLink(String id)
	{
		return new ODocumentPageLink(id, getModel()).setDocumentNameAsBody(true);
	}

}
