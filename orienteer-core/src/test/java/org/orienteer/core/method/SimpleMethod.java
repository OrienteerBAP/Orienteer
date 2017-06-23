package org.orienteer.core.method;

import org.apache.wicket.Component;

/**
 * 
 * This method annotated NOT as {@link SourceMethodDefinition} and NOT loaded into {@link SourceMethodDefinitionStorage} 
 *
 */

public class SimpleMethod implements IMethod {

	@Override
	public void methodInit(String id, IMethodEnvironmentData envData,IMethodConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getDisplayComponent() {
		// TODO Auto-generated method stub
		return null;
	}


}
