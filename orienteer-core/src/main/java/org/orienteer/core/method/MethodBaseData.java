package org.orienteer.core.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.IWidgetType;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Base Method environment data.
 * Any input parameters may be null.
 *
 */
public class MethodBaseData implements IMethodEnvironmentData{

	IModel<?> objModel;
	AbstractWidget<?> widget;
	String widgetType;
	IModel<DisplayMode> displayModeModel;
	
	public MethodBaseData(IModel<?> objModel,AbstractWidget<?> widget,IModel<DisplayMode> displayModeModel) {
		this.objModel = objModel;
		this.widget = widget;
		if (widget!=null){
			this.widgetType = widget.getWidgetDocument().field(OWidgetsModule.OPROPERTY_TYPE_ID);
		}
		this.displayModeModel = displayModeModel;
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


}
