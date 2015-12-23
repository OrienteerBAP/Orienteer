package org.orienteer.pages.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.SetModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.io.IClusterable;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.Strings;
import org.orienteer.pages.OPageParametersEncoder;
import org.orienteer.pages.module.PagesModule;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

/**
 * Delegate for functions which commonly used in wrapped pages
 */
public class PageDelegate implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider, IDetachable, IClusterable{
	
	private static final IMarkupResourceStreamProvider DEFAULT_MARKUP_PROVIDER = new DefaultMarkupResourceStreamProvider();
	
	private final WebPage page;
	private final ODocumentModel pageDocumentModel;
	
	public PageDelegate(WebPage page, PageParameters parameters) {
		this(page, parameters.get(OPageParametersEncoder.PAGE_IDENTITY).toString(), parameters.get("rid").toOptionalString());
	}
	
	public PageDelegate(WebPage page, String pageOrid, String docOrid) {
		this(page, new ORecordId(pageOrid), Strings.isEmpty(docOrid)?null:new ORecordId(docOrid));
	}
	
	public PageDelegate(WebPage page, ORID pageOrid, ORID docOrid) {
		this.page = page;
		this.pageDocumentModel = new ODocumentModel(pageOrid);
		ODocument doc = (ODocument)(docOrid!=null?docOrid.getRecord():pageDocumentModel.getObject().field(PagesModule.OPROPERTY_DOCUMENT));
		if(doc!=null) page.setDefaultModel(new ODocumentModel(doc));
	}

	@Override
	public void detach() {
		pageDocumentModel.detach();
	}
	
	public ODocumentModel getPageDocumentModel() {
		return pageDocumentModel;
	}

	@Override
	public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
		ODocument pageDoc = pageDocumentModel.getObject();
		return "OPage-"+pageDoc.getIdentity().toString()+"?v"+pageDoc.getVersion();
	}

	@Override
	public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass) {
		return getMarkupResourceStream(container, containerClass, null);
	}
	
	public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass, String wrapTag) {
		if(container.getClass().equals(containerClass)) {
			String content = (String) pageDocumentModel.getObject().field(PagesModule.OPROPERTY_CONTENT);
			if(!Strings.isEmpty(wrapTag)) content = "<"+wrapTag+">"+content+"</"+wrapTag+">";
			return new StringResourceStream(content, "text/html");
		} else {
			return DEFAULT_MARKUP_PROVIDER.getMarkupResourceStream(container, containerClass);
		}
	}
	
	public IModel<String> getTitleModel() {
		return new ODocumentPropertyModel<String>(pageDocumentModel, "title");
	}
	
}
