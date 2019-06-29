package org.orienteer.core.method.filters;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.IMethodContext;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * OFilter by OClass. Allow if we browse OClass documents with type "fData"
 * 
 *
 */

public class OClassBrowseFilter extends AbstractStringFilter{

	private static final Logger LOG = LoggerFactory.getLogger(OClassBrowseFilter.class);

	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		IModel<?> model = dataObject.getDisplayObjectModel();
		try {
			if (model!=null && model.getObject()!=null && model.getObject() instanceof OClass){
				return ((OClass) (model.getObject())).isSubClassOf(this.filterData);
			}
		} catch (Exception e) {
			LOG.error("Error during filtering", e);
		}

		return false;
	}
}
