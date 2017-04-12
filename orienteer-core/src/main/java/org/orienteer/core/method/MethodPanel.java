package org.orienteer.core.method;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.meta.IDisplayModeAware;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.widget.AbstractWidget;

/**
 * 
 * Panel for methods display.
 *
 */
public class MethodPanel extends Panel implements IDisplayModeAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IModel<DisplayMode> displayModeModel;
	private DisplayMode oldDisplayMode;
	private List<IMethod> methods;

	private IModel<?> displayObjectModel;

	private AbstractWidget<?> widget;
	
	private MethodPlace place;
	

	public MethodPanel(String id, IModel<?> displayObjectModel,MethodPlace place) {
		super(id, displayObjectModel);
		this.displayObjectModel = displayObjectModel;
		this.place = place;
		//this.setOutputMarkupId(true);
		//this.widget = widget;
		//add(UpdateOnDashboardDisplayModeChangeBehavior.INSTANCE);
		//add(UpdateOnActionPerformedEventBehavior.INSTANCE_ALL_CONTINUE);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		widget = findParent(AbstractWidget.class);
		if (widget instanceof IDisplayModeAware){
			setDisplayModeModel(((IDisplayModeAware)widget).getModeModel());
		}
	}
	
	public void setDisplayModeModel(IModel<DisplayMode> displayModeModel){
		this.displayModeModel = displayModeModel;
	}
	
	private void reloadMethods(){
		methods = MethodManager.get().getMethods(new MethodBaseData(displayObjectModel,widget, displayModeModel,place));
		RepeatingView methodsView;
		addOrReplace( methodsView = new RepeatingView("methods"));
		for ( IMethod method : methods) {
			methodsView.add(method.getDisplayComponent(methodsView.newChildId()));
		}
	}
	
	@Override
	protected void onBeforeRender() {
		//if parent object support display mode
		if (displayModeModel!=null){
			if (!displayModeModel.getObject().equals(oldDisplayMode)){
				reloadMethods();
				oldDisplayMode = displayModeModel.getObject();
			}
		//if parent object NOT support display mode
		}else{
			if (methods==null){
				reloadMethods();
			}
		}
		super.onBeforeRender();
	}

	@Override
	public IModel<DisplayMode> getModeModel() {
		return displayModeModel;
	}

	@Override
	public DisplayMode getModeObject() {
		return displayModeModel.getObject();
	}
	

}
