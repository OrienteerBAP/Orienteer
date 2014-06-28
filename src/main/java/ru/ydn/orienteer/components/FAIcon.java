package ru.ydn.orienteer.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

public class FAIcon extends WebMarkupContainer
{
	
	public FAIcon(String id, FAIconType iconType)
	{
		this(id, Model.of(iconType.getCssClass()));
	}
	
	public FAIcon(String id, String icon)
	{
		this(id, Model.of(icon));
	}

	public FAIcon(String id, IModel<String> model)
	{
		super(id, model);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(!Strings.isEmpty(getDefaultModelObjectAsString()));
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "i");
		tag.append("class", "fa "+getDefaultModelObjectAsString(), " "); 
		super.onComponentTag(tag);
	}
	
	
	
}
