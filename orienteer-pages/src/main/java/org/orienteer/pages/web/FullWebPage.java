package org.orienteer.pages.web;

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
import org.orienteer.pages.OPageParametersEncoder;
import org.orienteer.pages.module.PagesModule;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link WebPage} to show content as whole page
 */
public class FullWebPage extends WebPage implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider {
	
	private final PageDelegate delegate;

	public FullWebPage(PageParameters parameters) {
		super(parameters);
		delegate = new PageDelegate(parameters);
	}
	
	@Override
	public void detachModels() {
		super.detachModels();
		delegate.detach();
	}

	@Override
	public String getCacheKey(MarkupContainer container, Class<?> containerClass) {
		return delegate.getCacheKey(container, containerClass);
	}

	@Override
	public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass) {
		return delegate.getMarkupResourceStream(container, containerClass);
	}
	
}
