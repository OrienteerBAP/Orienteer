package org.orienteer.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.lang.Generics;
import org.orienteer.core.component.structuretable.StructureTable;
import org.orienteer.core.service.IMarkupProvider;
import org.wicketstuff.select2.Select2Choice;
import org.wicketstuff.select2.Select2MultiChoice;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 * Implementation of {@link IMarkupProvider}
 */
@Singleton
public class MarkupProvider implements IMarkupProvider
{
	private ConcurrentHashMap<Class<? extends Component>, IMarkupFragment> markupsCache = Generics.newConcurrentHashMap();
	private ConcurrentHashMap<Class<? extends Component>, IMarkupFragment> markupsMap = Generics.newConcurrentHashMap();
	
	public MarkupProvider()
	{
		registerMarkupContent(DropDownChoice.class, "<select wicket:id=\"component\" class=\"form-control\"></select>");
		registerMarkupContent(ListMultipleChoice.class, "<select wicket:id=\"component\" class=\"form-control\"></select>");
		registerMarkupContent(CheckBox.class, "<input type=\"checkbox\" wicket:id=\"component\"/>");
		registerMarkupContent(TextField.class, "<input type=\"text\" wicket:id=\"component\" class=\"form-control\"/>");
		registerMarkupContent(NumberTextField.class, "<input type=\"number\" wicket:id=\"component\" class=\"form-control\"/>");
		registerMarkupContent(TextArea.class, "<textarea wicket:id=\"component\" class=\"form-control\"></textarea>");
		registerMarkupContent(FormComponentPanel.class, "<div wicket:id=\"component\"></div>");
		registerMarkupContent(Panel.class, "<div wicket:id=\"component\"></div>");
		registerMarkupContent(AbstractLink.class, "<a wicket:id=\"component\"></a>");
		registerMarkupContent(StructureTable.class, "<table wicket:id=\"component\"></table>");
		registerMarkupContent(Select2MultiChoice.class, "<select wicket:id=\"component\" class=\"form-control\"/>");
		registerMarkupContent(Select2Choice.class, "<select wicket:id=\"component\" class=\"form-control\"/>");
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
							while(thisClass!=null && !input.equals(thisClass))
							{
								thisClass = thisClass.getSuperclass();
								ret++;
							}
							return ret;
						}
					}).min(candidates);
					ret = markupsMap.get(minParents);
				}
			}
			if(ret==null) ret = Markup.NO_MARKUP;
			markupsCache.put(componentClass, ret);
		}
		return ret!=Markup.NO_MARKUP?ret:null;
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
