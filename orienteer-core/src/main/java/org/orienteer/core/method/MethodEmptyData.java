package org.orienteer.core.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.AbstractWidget;

/**
 * 
 * Empty OMethod environment data. Not parameterized, always return null or false  
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
	public MethodPlace getPlace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getTableObject() {
		// TODO Auto-generated method stub
		return null;
	}
}
