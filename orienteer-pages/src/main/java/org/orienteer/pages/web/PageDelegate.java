package org.orienteer.pages.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
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
	
	private final ODocumentModel pageDocumentModel;
	
	public PageDelegate(PageParameters parameters) {
		this(parameters.get(OPageParametersEncoder.PAGE_IDENTITY).toString());
	}
	
	public PageDelegate(String orid) {
		this(new ORecordId(orid));
	}
	
	public PageDelegate(ORID orid) {
		pageDocumentModel = new ODocumentModel(orid);
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
