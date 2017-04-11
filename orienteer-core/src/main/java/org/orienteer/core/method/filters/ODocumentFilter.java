package org.orienteer.core.method.filters;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * Filter by ODocument class. Allow if we seen ODocument with type "fData"
 * 
 * example :  
 * 
 * fData="OUser"
 *
 */
public class ODocumentFilter implements IMethodFilter{
	
	String oClassName;

	@Override
	public void setFilterData(String filterData) {
		oClassName = filterData;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		IModel<?> model = dataObject.getDisplayObjectModel();
		if (model!=null && model.getObject()!=null && model.getObject() instanceof ODocument){
			if (((ODocument) (model.getObject())).getSchemaClass().isSubClassOf(oClassName)){
				return true;
			}
		};
		return false;
	}


}
