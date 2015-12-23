package org.orienteer.pages.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.IMarkupCacheKeyProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.IResourceStream;
import org.orienteer.core.web.OrienteerBasePage;
import org.orienteer.pages.module.PagesModule;

/**
 * {@link WebPage} to show content as embedded page
 */
public class EmbeddedWebPage extends OrienteerBasePage<Object> implements IMarkupResourceStreamProvider, IMarkupCacheKeyProvider {
	
	private final PageDelegate delegate;

	public EmbeddedWebPage(PageParameters parameters) {
		super(parameters);
		delegate = new PageDelegate(this, parameters);
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
		return delegate.getMarkupResourceStream(container, containerClass, "wicket:extend");
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return delegate.getTitleModel();
	}
	
}
