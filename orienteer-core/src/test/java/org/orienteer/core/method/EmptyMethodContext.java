package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.widget.AbstractWidget;

public class EmptyMethodContext implements IMethodContext {
	
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
		return MethodPlace.DASHBOARD_SETTINGS;
	}

	@Override
	public Component getRelatedComponent() {
		// TODO Auto-generated method stub
		return null;
	}
}
