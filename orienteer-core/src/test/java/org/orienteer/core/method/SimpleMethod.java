package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.orienteer.core.method.definitions.JavaClassOMethodDefinition;

/**
 * 
 * This method annotated NOT as {@link JavaClassOMethodDefinition} and NOT loaded into {@link SourceMethodDefinitionStorage} 
 *
 */

public class SimpleMethod implements IMethod {

	@Override
	public void methodInit(String id, IMethodContext envData,IMethodDefinition config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getDisplayComponent() {
		// TODO Auto-generated method stub
		return null;
	}


}
