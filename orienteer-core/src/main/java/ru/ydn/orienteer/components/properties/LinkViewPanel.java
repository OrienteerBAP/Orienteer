package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.ODocumentPageLink;

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
