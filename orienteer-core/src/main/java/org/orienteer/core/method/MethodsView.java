package org.orienteer.core.method;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.IBootstrapAware;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.widget.AbstractWidget;

/**
 * 
 * Panel for methods display.
 *
 */
public class MethodsView implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<IMethod> methods;

	private MethodPlace place;
	private RepeatingView externalList;
	private IModel<?> displayObjectModel;
	private Component table;

	private BootstrapType bootstrapType;
	private boolean bootstrapTypeOverriden = false;
	
	public MethodsView(RepeatingView externalList, IModel<?> displayObjectModel,MethodPlace place, Component table) {
		this.externalList = externalList;
		this.displayObjectModel = displayObjectModel;
		this.place = place;
		this.table = table;
	}
	
	public void loadMethods(){
		AbstractWidget<?> widget = externalList.findParent(AbstractWidget.class);
		methods = MethodManager.get().getMethods(new MethodContext(displayObjectModel,widget,place,table));
		for ( IMethod method : methods) {
			Command<?> component = method.createCommand(); 
			if (component !=null){
				if (component instanceof IBootstrapAware && bootstrapTypeOverriden){
					((IBootstrapAware)component).setBootstrapType(bootstrapType);
				}
				externalList.add(component);
			}
		}
	}
	
	/**
	 * Use it only before loadMethods
	 * @param bootstrapType - {@link BootstrapType} to visualize
	 */	
	public MethodsView overrideBootstrapType(BootstrapType bootstrapType){
		this.bootstrapType = bootstrapType;
		bootstrapTypeOverriden = true;
		return this;
	}


	public List<IMethod> getMethods() {
		return methods;
	}
	
}
