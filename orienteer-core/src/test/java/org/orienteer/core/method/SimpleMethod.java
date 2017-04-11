package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

/**
 * 
 * This method annotated NOT as {@link SourceMethodDefinition} and NOT loaded into {@link SourceMethodDefinitionStorage} 
 *
 */

public class SimpleMethod implements IMethod {

	@Override
	public Component getDisplayComponent(String componentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IMethodEnvironmentData envData) {
		// TODO Auto-generated method stub
		
	}
}
