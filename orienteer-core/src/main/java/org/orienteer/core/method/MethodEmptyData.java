package org.orienteer.core.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.AbstractWidget;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * Empty Method environment data. Not parameterized, always return null or false  
 *
 */
public class MethodEmptyData implements IMethodEnvironmentData{

	@Override
	public IModel<?> getDisplayObjectModel() {
		return null;
	}

	@Override
	public AbstractWidget<?> getCurrentWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentWidgetType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel<DisplayMode> getDisplayModeModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
