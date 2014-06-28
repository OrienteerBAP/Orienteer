package ru.ydn.orienteer.components.properties;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.orienteer.model.DocumentNameModel;
import ru.ydn.orienteer.web.DocumentPage;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LinkViewPanel<M extends OIdentifiable> extends GenericPanel<M> {

	public LinkViewPanel(
			String id,
			IModel<M> valueModel) {
		super(id, valueModel);
		add(new ODocumentPageLink("link", getModel()).setDocumentNameAsBody(true));
	}

}
