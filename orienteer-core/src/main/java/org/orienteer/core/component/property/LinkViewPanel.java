package org.orienteer.core.component.property;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.IExportable;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.model.ODocumentNameModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Panel} to view a link to a document
 */
public class LinkViewPanel extends AbstractLinkViewPanel<ODocument> implements IExportable<Object> {

	private static final long serialVersionUID = 1L;
	
	private IModel<?> titleModel;

	public LinkViewPanel(
			String id,
			IModel<ODocument> valueModel) {
		this(id, valueModel, null);
	}
	
	public LinkViewPanel(
			String id,
			IModel<ODocument> valueModel, IModel<?> titleModel) {
		super(id, valueModel);
		this.titleModel = titleModel;
	}
	
	@Override
	protected AbstractLink newLink(String id)
	{
		ODocumentPageLink link = new ODocumentPageLink(id, getModel());
		if(titleModel!=null) link.setBody(titleModel);
		else link.setDocumentNameAsBody(true);
		return link;
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(titleModel!=null) titleModel.detach();
	}

	@Override
	public IModel<?> getExportableDataModel() {
		if(link!=null) return (IModel<String>) link.getBody();
		else if(titleModel!=null) return titleModel;
		else return new ODocumentNameModel(getModel());
	}

}
