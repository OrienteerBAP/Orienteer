package org.orienteer.core.method.filters;

import org.apache.wicket.model.IModel;
import org.orienteer.core.method.IMethodContext;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * OFilter by ODocument class. Allow if we seen ODocument with type "fData"
 *
 */
public class ODocumentFilter extends AbstractStringFilter{

	private static final Logger LOG = LoggerFactory.getLogger(ODocumentFilter.class);

	@Override
	public boolean isSupportedMethod(IMethodContext dataObject) {
		IModel<?> model = dataObject.getDisplayObjectModel();
		try {
			if (model!=null && model.getObject()!=null && model.getObject() instanceof ODocument){
				OClass schemaClass = ((ODocument) (model.getObject())).getSchemaClass();
				return schemaClass!=null?schemaClass.isSubClassOf(this.filterData):false;
			}
		} catch (Exception e) {
			LOG.error("Error for model: {} and context: {}", model, dataObject, e);
		}

		return false;
	}


}
