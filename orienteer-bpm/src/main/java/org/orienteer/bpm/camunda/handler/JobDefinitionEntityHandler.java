package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.JobDefinitionEntity;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class JobDefinitionEntityHandler extends AbstractEntityHandler<JobDefinitionEntity> {

	public JobDefinitionEntityHandler() {
		super("BPMJobDefinition");
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("processDefinitionId", OType.STRING, 10)
			  .oProperty("processDefinitionKey", OType.STRING, 20)
			  .oProperty("activityId", OType.STRING, 30)
			  .oProperty("jobType", OType.STRING, 40)
			  .oProperty("jobConfiguration", OType.STRING, 50)
			  .oProperty("jobPriority", OType.LONG, 60)
			  .oProperty("suspensionState", OType.INTEGER, 70);
	}
	
	@Override
	protected void initMapping(ODatabaseDocument db) {
		super.initMapping(db);
		mappingFromEntityToDoc.remove("jobPriority");
		mappingFromEntityToDoc.put("overridingJobPriority", "jobPriority");
	}

	
}
