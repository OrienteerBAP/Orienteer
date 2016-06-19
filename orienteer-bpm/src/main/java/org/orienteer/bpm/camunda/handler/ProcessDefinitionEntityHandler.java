package org.orienteer.bpm.camunda.handler;


import java.util.List;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
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
	
	@Statement
	public List<ProcessDefinitionEntity> selectLatestProcessDefinitionByKey(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where key = ? order by @rid desc limit 1", param.getParameter());
	}

}
