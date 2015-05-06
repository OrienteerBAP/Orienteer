package org.orienteer.core.service;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.orienteer.core.service.impl.MarkupProvider;

import com.google.inject.ImplementedBy;

/**
 * Guice service to provide markup per component.
 * Widely used for meta components
 */
@ImplementedBy(MarkupProvider.class)
public interface IMarkupProvider
{
	public void registerMarkupContent(Class<? extends Component> componentClass, String content);
	public void registerMarkupContent(Class<? extends Component> componentClass, IMarkupFragment markup);
	public IMarkupFragment provideMarkup(Component component);
	public IMarkupFragment provideMarkup(Class<? extends Component> componentClass);
}
