package org.orienteer.core.method.filters;

import org.orienteer.core.method.IMethodEnvironmentData;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * Filter by OClass of current ODocument or OClass
 *
 */

public class OEntityFilter extends AbstractStringFilter {

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		OClass oclass = getOClass(dataObject);
		if (oclass!=null){
			return oclass.isSubClassOf(this.filterData);
		}
		return false;
	}
	
	private OClass getOClass(IMethodEnvironmentData dataObject){
		if (dataObject.getDisplayObjectModel()!=null){
			Object obj = dataObject.getDisplayObjectModel().getObject();
			if (obj instanceof OClass){
				return (OClass) obj;
			}else if (obj instanceof ODocument){
				return ((ODocument)obj).getSchemaClass();
			}
		}
		return null;
	}

}
