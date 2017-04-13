package org.orienteer.core.method;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.IBootstrapAware;
import org.orienteer.core.widget.AbstractWidget;

/**
 * 
 * Panel for methods display.
 *
 */
public class MethodsView extends RepeatingView{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<IMethod> methods;

	private AbstractWidget<?> widget;
	
	private MethodPlace place;

	private BootstrapType bootstrapType;
	private boolean bootstrapTypeOverriden = false;
	
	public MethodsView(String id, IModel<?> displayObjectModel,MethodPlace place) {
		super(id, displayObjectModel);
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
		loadMethods();
	}
	
	private void loadMethods(){
		methods = MethodManager.get().getMethods(new MethodBaseData(getDefaultModel(),widget,place));
		for ( IMethod method : methods) {
			Component component = method.getDisplayComponent(newChildId()); 
			if (component instanceof IBootstrapAware && bootstrapTypeOverriden){
				((IBootstrapAware)component).setBootstrapType(bootstrapType);
			}
			add(component);
		}
	}
	
	/**
	 * Use it before onInitialize
	 * @param type
	 */	
	public MethodsView overrideBootstrapType(BootstrapType bootstrapType){
		this.bootstrapType = bootstrapType;
		bootstrapTypeOverriden = true;
		return this;
	}
	
}
