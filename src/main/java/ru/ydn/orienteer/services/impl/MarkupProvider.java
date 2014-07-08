package ru.ydn.orienteer.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.lang.Generics;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import ru.ydn.orienteer.services.IMarkupProvider;

@Singleton
public class MarkupProvider implements IMarkupProvider
{
	private ConcurrentHashMap<Class<? extends Component>, IMarkupFragment> markupsCache = Generics.newConcurrentHashMap();
	private ConcurrentHashMap<Class<? extends Component>, IMarkupFragment> markupsMap = Generics.newConcurrentHashMap();
	
	public MarkupProvider()
	{
		registerMarkupContent(DropDownChoice.class, "<select wicket:id=\"component\" class=\"form-control\"></select>");
		registerMarkupContent(CheckBox.class, "<input type=\"checkbox\" wicket:id=\"component\"/>");
		registerMarkupContent(TextField.class, "<input type=\"text\" wicket:id=\"component\" class=\"form-control\"/>");
	}
	
	@Override
	public IMarkupFragment provideMarkup(Component component) {
		return provideMarkup(component.getClass());
	}

	@Override
	public IMarkupFragment provideMarkup(
			final Class<? extends Component> componentClass) {
		IMarkupFragment ret = markupsCache.get(componentClass);
		if(ret==null)
		{
			ret = markupsMap.get(componentClass);
			if(ret==null)
			{
				List<Class<? extends Component>> candidates = new ArrayList<Class<? extends Component>>();
				for (Map.Entry<Class<? extends Component>, IMarkupFragment> entry : markupsMap.entrySet())
				{
					if(entry.getKey().isAssignableFrom(componentClass)) candidates.add(entry.getKey());
				}
				if(candidates.size()==1)
				{
					ret = markupsMap.get(candidates.get(0));
				}
				else if(candidates.size()>1)
				{
					Class<? extends Component> minParents = Ordering.<Integer>natural().onResultOf(new Function<Class<? extends Component>, Integer>() {

						@Override
						public Integer apply(Class<? extends Component> input) {
							int ret = 0;
							Class<?> thisClass = componentClass;
							while(input!=null && !input.equals(thisClass))
							{
								thisClass = thisClass.getSuperclass();
							}
							return ret;
						}
					}).min(candidates);
					ret = markupsMap.get(minParents);
				}
			}
		}
		if(ret!=null) markupsCache.put(componentClass, ret);
		return ret;
	}

	@Override
	public void registerMarkupContent(
			Class<? extends Component> componentClass, String content) {
		registerMarkupContent(componentClass, Markup.of(content));
	}

	@Override
	public void registerMarkupContent(
			Class<? extends Component> componentClass, IMarkupFragment markup) {
		markupsMap.put(componentClass, markup);
		markupsCache.clear();
	}


}
