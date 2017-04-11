package org.orienteer.core.method;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.IWidgetType;

public class MethodPanel extends Panel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IModel<DisplayMode> displayModeModel;
	private DisplayMode oldDisplayMode;
	private List<IMethod> methods;

	private IModel<?> displayObjectModel;

	private AbstractWidget<?> widget;
	

	public MethodPanel(String id, IModel<?> displayObjectModel,AbstractWidget<?> widget) {
		super(id, displayObjectModel);
		this.displayObjectModel = displayObjectModel;
		this.widget = widget;
		reloadMethods();
	}
	
	
	public void setDisplayModeModel(IModel<DisplayMode> displayModeModel){
		this.displayModeModel = displayModeModel;
	}
	
	private void reloadMethods(){
		methods = MethodManager.get().getMethods(new MethodBaseData(displayObjectModel,widget, displayModeModel));
		RepeatingView methodsView;
		addOrReplace( methodsView = new RepeatingView("methods"));
		for ( IMethod method : methods) {
			methodsView.add(method.getDisplayComponent(methodsView.newChildId()));
		}
	}
	
	@Override
	protected void onBeforeRender() {
		if (displayModeModel!=null && !displayModeModel.getObject().equals(oldDisplayMode)){
			reloadMethods();
		}
		super.onBeforeRender();
	}
	

}
