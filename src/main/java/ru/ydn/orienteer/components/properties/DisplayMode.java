package ru.ydn.orienteer.components.properties;

import org.apache.wicket.Page;

import ru.ydn.orienteer.web.ViewDocumentPage;

public enum DisplayMode {
	VIEW(ViewDocumentPage.class), EDIT(ViewDocumentPage.class); //TODO: Change EDIT page
	
	private final Class<? extends Page> defaultPageClass;
	
	private DisplayMode(Class<? extends Page> defaultPageClass)
	{
		this.defaultPageClass = defaultPageClass;
	}

	public Class<? extends Page> getDefaultPageClass() {
		return defaultPageClass;
	}
	
	
}
