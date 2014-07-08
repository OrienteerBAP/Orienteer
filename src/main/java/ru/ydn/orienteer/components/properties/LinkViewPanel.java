package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import ru.ydn.orienteer.components.ODocumentPageLink;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

public class LinkViewPanel<M extends OIdentifiable> extends AbstractLinkViewPanel<M> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LinkViewPanel(
			String id,
			IModel<M> valueModel) {
		super(id, valueModel);
	}
	
	@Override
	protected AbstractLink newLink(String id)
	{
		return new ODocumentPageLink<M>(id, getModel()).setDocumentNameAsBody(true);
	}

}
