package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class ResourceEntityHandler extends AbstractEntityHandler<ResourceEntity> {

	public ResourceEntityHandler() {
		super("BPMResource");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 0)
			  .oProperty("deploymentId", OType.STRING, 10)
			  .oProperty("bytes", OType.BINARY, 20)
			  .oProperty("generated", OType.BOOLEAN, 40);
	}

}
