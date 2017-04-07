package org.orienteer.core.method;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

public class MethodPanel extends Panel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	IModel<DisplayMode> displayModeModel;
	List<IMethod> methods;
	

	public MethodPanel(String id, IModel<?> displayObjectModel) {
		super(id, displayObjectModel);
	
		methods = MethodManager.get().getMethods(new MethodBaseData(displayObjectModel));
		RepeatingView methodsView;
		add( methodsView = new RepeatingView("methods"));
		for ( IMethod method : methods) {
			methodsView.add(method.getDisplayComponent(methodsView.newChildId()));
		}
	}
	
	public void setDisplayModeModel(IModel<DisplayMode> displayModeModel){
		this.displayModeModel = displayModeModel;
		for (IMethod iMethod : methods) {
			iMethod.setDisplayModeModel(displayModeModel);
		}
	}
	

}
