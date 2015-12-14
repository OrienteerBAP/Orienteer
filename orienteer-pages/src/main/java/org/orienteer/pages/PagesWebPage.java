package org.orienteer.pages;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.orienteer.pages.module.PagesModule;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Orienteer Pages {@link WebPage}
 */
public class PagesWebPage extends WebPage implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider {
	
	private final ODocumentModel pageDocumentModel;

	public PagesWebPage(PageParameters parameters) {
		super(parameters);
		String orid = parameters.get(OPageParametersEncoder.PAGE_IDENTITY).toString();
		pageDocumentModel = new ODocumentModel(new ORecordId(orid));
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		pageDocumentModel.detach();
	}

	@Override
	public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
		return "OPage-"+pageDocumentModel.getIdentity().toString();
	}

	@Override
	public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass) {
		return new StringResourceStream((String) pageDocumentModel.getObject().field(PagesModule.OPROPERTY_CONTENT), "text/html");
	}
	
	
}
