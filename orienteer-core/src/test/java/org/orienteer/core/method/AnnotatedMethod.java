package org.orienteer.core.method;

import org.apache.wicket.Component;

/**
 * 
 * This method annotated as {@link SourceMethodDefinition} and loaded into {@link SourceMethodDefinitionStorage} 
 *
 */

@OMethod(filters = { 
			@OFilter(fClass = TestFilter.class, fData = "testData" ) 
		})
public class AnnotatedMethod implements IMethod{

	@Override
	public void methodInit(String id, IMethodContext envData,IMethodConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getDisplayComponent() {
		// TODO Auto-generated method stub
		return null;
	}




}
