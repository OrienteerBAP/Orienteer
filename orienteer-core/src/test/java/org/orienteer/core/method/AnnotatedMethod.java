package org.orienteer.core.method;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.filters.DisallowFilter;

/**
 * 
 * This method annotated as {@link SourceMethodDefinition} and loaded into {@link SourceMethodDefinitionStorage} 
 *
 */

@Method(filters = { 
			@Filter(fClass = TestFilter.class, fData = "testData" ) 
		})
public class AnnotatedMethod implements IMethod{


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
