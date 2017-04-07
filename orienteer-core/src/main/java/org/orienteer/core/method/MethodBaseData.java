package org.orienteer.core.method;

import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Base Method environment data.
 * Any input parameters may be null.
 *
 */
public class MethodBaseData implements IMethodEnvironmentData{

	IModel<?> objModel;
	
	public MethodBaseData(IModel<?> objModel) {
		this.objModel = objModel;
	}

	@Override
	public IModel<?> getDisplayObjectModel() {
		return objModel;
	}
}
