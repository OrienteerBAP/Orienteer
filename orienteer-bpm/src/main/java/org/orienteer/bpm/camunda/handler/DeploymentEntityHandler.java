package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class DeploymentEntityHandler extends AbstractEntityHandler<DeploymentEntity> {

	public DeploymentEntityHandler() {
		super("BPMDeployment");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 0)
			  .oProperty("deploymentTime", OType.DATETIME, 30);
	}

}
