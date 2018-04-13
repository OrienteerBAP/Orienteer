package org.orienteer.core.method.filters;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.IMethodContext;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * OFilter by ODocument class. Allow if we seen ODocument with type "fData"
 *
 */
public class ODocumentFilter extends AbstractStringFilter{
	
	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		IModel<?> model = dataObject.getDisplayObjectModel();
		if (model!=null && model.getObject()!=null && model.getObject() instanceof ODocument){
			return ((ODocument) (model.getObject())).getSchemaClass().isSubClassOf(this.filterData);
		}
		return false;
	}


}
