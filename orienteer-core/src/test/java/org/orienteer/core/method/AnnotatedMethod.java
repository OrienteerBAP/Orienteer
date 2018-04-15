package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.orienteer.core.method.definitions.JavaClassOMethodDefinition;

/**
 * 
 * This method annotated as {@link JavaClassOMethodDefinition} and loaded into {@link SourceMethodDefinitionStorage} 
 *
 */

@OMethod(filters = { 
			@OFilter(fClass = TestFilter.class, fData = "testData" ) 
		})
public class AnnotatedMethod implements IMethod{

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
