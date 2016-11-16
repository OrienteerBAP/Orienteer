package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.JobDefinitionEntity;
import org.camunda.bpm.engine.management.JobDefinitionQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.history.HistoricDetailEventEntityHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricIncidentEventEntityHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricJobLogEventEntityHandler;
import org.orienteer.bpm.camunda.handler.history.UserOperationLogEntryEventEntityHandler;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

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
		helper.domain(OClassDomain.SYSTEM);
		helper.oProperty("processDefinition", OType.LINK, 10).assignVisualization("listbox")
			  .oProperty("processDefinitionKey", OType.STRING, 20)
			  .oProperty("activityId", OType.STRING, 30)
			  .oProperty("jobType", OType.STRING, 40)
			  .oProperty("jobConfiguration", OType.STRING, 50)
			  .oProperty("jobPriority", OType.LONG, 60)
			  .oProperty("suspensionState", OType.INTEGER, 70)
			  .oProperty("historyIncidentEvents", OType.LINKLIST, 80).assignVisualization("table")
			  .oProperty("historyJobLogEvents", OType.LINKLIST, 90).assignVisualization("table")
			  .oProperty("userOperationLogEntryEvents", OType.LINKLIST, 100).assignVisualization("table");
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(JobDefinitionEntityHandler.OCLASS_NAME, "processDefinition", ProcessDefinitionEntityHandler.OCLASS_NAME);
		helper.setupRelationship(OCLASS_NAME, "historyIncidentEvents", HistoricIncidentEventEntityHandler.OCLASS_NAME, "jobDefinition");
		helper.setupRelationship(OCLASS_NAME, "historyJobLogEvents", HistoricJobLogEventEntityHandler.OCLASS_NAME, "jobDefinition");
		helper.setupRelationship(OCLASS_NAME, "userOperationLogEntryEvents", UserOperationLogEntryEventEntityHandler.OCLASS_NAME, "jobDefinition");
	}

	@Override
	protected void initMapping(OPersistenceSession session) {
		super.initMapping(session);
		mappingFromEntityToDoc.remove("jobPriority");
		mappingFromEntityToDoc.put("overridingJobPriority", "jobPriority");
	}
	
	@Statement
	public List<JobDefinitionEntity> selectJobDefinitionsByProcessDefinitionId(OPersistenceSession session, ListQueryParameterObject query) {
		return queryList(session, "select from "+getSchemaClass()+" where processDefinition.id = ?", query.getParameter());
	}
	
	@Statement
	public List<JobDefinitionEntity> selectJobDefinitionByQueryCriteria(OPersistenceSession session, JobDefinitionQuery query) {
		return query(session, query);
	}
	
	@Statement
	public void deleteJobDefinitionsByProcessDefinitionId(OPersistenceSession session, String processDefinitionId) {
		command(session, "delete from "+getSchemaClass()+" where processDefinition.id = ?", processDefinitionId);
	}

	
}
