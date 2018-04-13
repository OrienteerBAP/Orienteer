package org.orienteer.core.method;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.AbstractWidget;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Base OMethod context object.
 * Any input parameters may be null.
 *
 */
public class MethodContext implements Serializable,IMethodContext{

	private static final long serialVersionUID = 1L;
	
	private IModel<?> objModel;
	private AbstractWidget<?> widget;
	private String widgetType;
	private MethodPlace place;
	private Object tableObject;
	
	public MethodContext(IModel<?> objModel,AbstractWidget<?> widget,MethodPlace place,Object tableObject) {
		this.objModel = objModel;
		this.widget = widget;
		if (widget!=null){
			ODocument widgetDoc = widget.getWidgetDocument();
			if(widgetDoc!=null) this.widgetType = widgetDoc.field(OWidgetsModule.OPROPERTY_TYPE_ID);
		}
		this.place = place;
		this.tableObject = tableObject;
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
	public MethodPlace getPlace() {
		return place;
	}

	@Override
	public Object getTableObject() {
		return tableObject;
	}
}
