package org.orienteer.pages;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Orienteer Pages {@link WebPage}
 */
public class PagesWebPage extends WebPage {
	
	private final IModel<ODocument> pageDocumentModel;

	public PagesWebPage(PageParameters parameters) {
		super(parameters);
		String orid = parameters.get(OPageParametersEncoder.PAGE_IDENTITY).toString();
		pageDocumentModel = new ODocumentModel(new ORecordId(orid));
		add(new Label("content", new ODocumentPropertyModel<String>(pageDocumentModel, "content")));
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		pageDocumentModel.detach();
	}
	
	
}
