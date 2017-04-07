package org.orienteer.core.method.filters;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.IMethodFilter;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * Filter by OClass. Allow if we browse OClass documents with type "filterData"
 * 
 * example :  
 * 
 * filterData="OUser"
 *
 */

public class OClassBrowseFilter implements IMethodFilter{

	String oClassName;

	@Override
	public void setFilterData(String filterData) {
		oClassName = filterData;
	}

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		IModel<?> model = dataObject.getDisplayObjectModel();
		if (model!=null && model.getObject()!=null && model.getObject() instanceof OClass){
			if (((OClass) (model.getObject())).getName().equals(oClassName)){
				return true;
			}
		};
		return false;
	}


}
