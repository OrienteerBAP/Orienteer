package ru.ydn.orienteer.components.properties;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.orienteer.web.DocumentPage;

public enum DisplayMode {
	VIEW(DocumentPage.class, false),
	EDIT(DocumentPage.class, true); //TODO: Change EDIT page
	
	private final Class<? extends Page> defaultPageClass;
	private final boolean canModify;
	
	private DisplayMode(Class<? extends Page> defaultPageClass, boolean canModify)
	{
		this.defaultPageClass = defaultPageClass;
		this.canModify = canModify;
	}

	public Class<? extends Page> getDefaultPageClass() {
		return defaultPageClass;
	}
	
	public String getName()
	{
		return name().toLowerCase();
	}
	
	public boolean canModify()
	{
		return canModify;
	}
	
	public IModel<DisplayMode> asModel()
	{
		return Model.of(this);
	}
	
	public static DisplayMode parse(String string)
	{
		if(string==null) return null;
		try {
			return DisplayMode.valueOf(string.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
}
