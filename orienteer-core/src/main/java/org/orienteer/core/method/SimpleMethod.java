package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

@Method
public class SimpleMethod extends Panel implements IMethod{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	IModel<DisplayMode> displayModeModel;

	public SimpleMethod() {
		super("temporaryId");
	}
	
	@Override
	public void setDisplayModeModel(IModel<DisplayMode> displayModeModel) {
		this.displayModeModel = displayModeModel;
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		setMarkupId(componentId);
		return this;
	}

}
