package org.orienteer.core.component.property;

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.orienteer.core.web.ODocumentPage;

/**
 * Orienteer's display modes. There are 2 types: VIEW and EDIT.
 * Some new might be added later
 */
public enum DisplayMode {
	VIEW(ODocumentPage.class, false),
	EDIT(ODocumentPage.class, true);
	
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
