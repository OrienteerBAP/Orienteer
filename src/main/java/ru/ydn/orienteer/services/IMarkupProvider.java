package ru.ydn.orienteer.services;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;

import ru.ydn.orienteer.services.impl.MarkupProvider;

import com.google.inject.ImplementedBy;

@ImplementedBy(MarkupProvider.class)
public interface IMarkupProvider
{
	public void registerMarkupContent(Class<? extends Component> componentClass, String content);
	public void registerMarkupContent(Class<? extends Component> componentClass, IMarkupFragment markup);
	public IMarkupFragment provideMarkup(Component component);
	public IMarkupFragment provideMarkup(Class<? extends Component> componentClass);
}
