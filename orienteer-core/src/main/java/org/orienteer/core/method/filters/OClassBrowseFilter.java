package org.orienteer.core.method.filters;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.IMethodEnvironmentData;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * 
 * OFilter by OClass. Allow if we browse OClass documents with type "fData"
 * 
 *
 */

public class OClassBrowseFilter extends AbstractStringFilter{

	@Override
	public boolean isSupportedMethod(IMethodEnvironmentData dataObject) {
		IModel<?> model = dataObject.getDisplayObjectModel();
		if (model!=null && model.getObject()!=null && model.getObject() instanceof OClass){
			return ((OClass) (model.getObject())).isSubClassOf(this.filterData);
		}
		return false;
	}
}
