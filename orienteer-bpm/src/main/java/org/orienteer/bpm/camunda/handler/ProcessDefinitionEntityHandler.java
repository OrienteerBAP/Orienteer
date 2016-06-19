package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class ProcessDefinitionEntityHandler extends AbstractEntityHandler<ProcessDefinitionEntity> {

	public ProcessDefinitionEntityHandler() {
		super("BPMProcessDefinition");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("key", OType.STRING, 30)
			  .oProperty("category", OType.STRING, 40)
			  .oProperty("name", OType.STRING, 50)
			  .oProperty("deploymentId", OType.STRING, 60)
			  .oProperty("suspensionState", OType.INTEGER, 70);
	}

}
