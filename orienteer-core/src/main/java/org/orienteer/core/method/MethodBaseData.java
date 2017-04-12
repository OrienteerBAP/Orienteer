package org.orienteer.core.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.AbstractWidget;

/**
 * Base Method environment data.
 * Any input parameters may be null.
 *
 */
public class MethodBaseData implements IMethodEnvironmentData{

	private IModel<?> objModel;
	private AbstractWidget<?> widget;
	private String widgetType;
	private IModel<DisplayMode> displayModeModel;
	private MethodPlace place;
	
	public MethodBaseData(IModel<?> objModel,AbstractWidget<?> widget,IModel<DisplayMode> displayModeModel,MethodPlace place) {
		this.objModel = objModel;
		this.widget = widget;
		if (widget!=null){
			this.widgetType = widget.getWidgetDocument().field(OWidgetsModule.OPROPERTY_TYPE_ID);
		}
		this.displayModeModel = displayModeModel;
		this.place = place;
	}

	@Override
	public IModel<?> getDisplayObjectModel() {
		return objModel;
	}

	@Override
	public AbstractWidget<?> getCurrentWidget() {
		return widget;
	}

	@Override
	public String getCurrentWidgetType() {
		return widgetType;
	}

	@Override
	public IModel<DisplayMode> getDisplayModeModel() {
		return displayModeModel;
	}

	@Override
	public MethodPlace getPlace() {
		return place;
	}


}
