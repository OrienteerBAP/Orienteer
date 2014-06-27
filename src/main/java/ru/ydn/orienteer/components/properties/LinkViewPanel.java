package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.orienteer.model.DocumentNameModel;
import ru.ydn.orienteer.web.DocumentPage;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinkViewPanel extends PropertyViewPanel<OIdentifiable> {

	public LinkViewPanel(
			String id,
			IModel<ODocument> documentModel,
			IModel<OProperty> propertyModel) {
		super(id, documentModel, propertyModel);
		add(new ODocumentPageLink("link", getValueModel()).setDocumentNameAsBody(true));
	}

}
