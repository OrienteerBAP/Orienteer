package org.orienteer.bpm.camunda.handler;

import java.util.List;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.JobDefinitionEntity;
import org.camunda.bpm.engine.management.JobDefinitionQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IEntityHandler} for {@link JobDefinitionEntity} 
 */
public class JobDefinitionEntityHandler extends AbstractEntityHandler<JobDefinitionEntity> {

	public static final String OCLASS_NAME = "BPMJobDefinition";
	
	public JobDefinitionEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("processDefinitions", OType.LINK, 10)
			  .oProperty("processDefinitionKey", OType.STRING, 20)
			  .oProperty("activityId", OType.STRING, 30)
			  .oProperty("jobType", OType.STRING, 40)
			  .oProperty("jobConfiguration", OType.STRING, 50)
			  .oProperty("jobPriority", OType.LONG, 60)
			  .oProperty("suspensionState", OType.INTEGER, 70);
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(JobDefinitionEntityHandler.OCLASS_NAME, "processDefinitions", JobEntityHandler.OCLASS_NAME);
	}

	@Override
	protected void initMapping(OPersistenceSession session) {
		super.initMapping(session);
		mappingFromEntityToDoc.remove("jobPriority");
		mappingFromEntityToDoc.put("overridingJobPriority", "jobPriority");
	}
	
	@Statement
	public List<JobDefinitionEntity> selectJobDefinitionsByProcessDefinitionId(OPersistenceSession session, ListQueryParameterObject query) {
		return queryList(session, "select from "+getSchemaClass()+" where processDefinitions.id = ?", query.getParameter());
	}
	
	@Statement
	public List<JobDefinitionEntity> selectJobDefinitionByQueryCriteria(OPersistenceSession session, JobDefinitionQuery query) {
		return query(session, query);
	}
	
	@Statement
	public void deleteJobDefinitionsByProcessDefinitionId(OPersistenceSession session, String processDefinitionId) {
		command(session, "delete from "+getSchemaClass()+" where processDefinitions.id = ?", processDefinitionId);
	}

	
}
