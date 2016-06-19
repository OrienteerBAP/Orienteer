package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.PropertyEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class PropertyEntityHandler extends AbstractEntityHandler<PropertyEntity> {

	
	public PropertyEntityHandler() {
		super("BPMProperty");
	}

	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 30)
			  .oProperty("value", OType.STRING, 40);
	}

}
