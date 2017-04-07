package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.filters.DisallowFilter;

@Method(filter=DisallowFilter.class )
public class AnnotatedMethod implements IMethod{

	@Override
	public void setDisplayModeModel(IModel<DisplayMode> displayModeModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getDisplayComponent(String componentId) {
		// TODO Auto-generated method stub
		return null;
	}



}
