package org.orienteer.bpm.camunda.handler;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.externaltask.ExternalTaskQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.ByteArrayEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExternalTaskEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import com.github.raymanrt.orientqb.query.Query;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IEntityHandler} for {@link ExternalTaskEntity} 
 */
public class ExternalTaskEntityHandler extends AbstractEntityHandler<ExternalTaskEntity> {
	
	public static final String OCLASS_NAME="BPMExternalTask";
	
	public ExternalTaskEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.domain(OClassDomain.SYSTEM);
		helper.oProperty("topicName", OType.STRING, 10).markAsDocumentName().markDisplayable()
			  .oProperty("workerId", OType.STRING, 20).markDisplayable()
			  .oProperty("retries", OType.INTEGER, 30).markDisplayable()
			  .oProperty("errorMessage", OType.STRING, 40)
			  .oProperty("lockExpirationTime", OType.DATETIME, 50)
			  .oProperty("suspensionState", OType.INTEGER, 60)
			  .oProperty("execution", OType.LINK, 70).assignVisualization("listbox")
			  .oProperty("processInstanceId", OType.STRING, 80).markDisplayable()
			  .oProperty("processDefinition", OType.LINK, 90).assignVisualization("listbox").markDisplayable()
			  .oProperty("processDefinitionKey", OType.STRING, 100)
			  .oProperty("activityId", OType.STRING, 110)
			  .oProperty("activityInstanceId", OType.STRING, 120)
//			  .oProperty("tenantId", OType.STRING, 130) // Tenants are not supported
			  .oProperty("priority", OType.LONG, 140).markDisplayable();
	}

	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(ExternalTaskEntityHandler.OCLASS_NAME, "processDefinition", ProcessDefinitionEntityHandler.OCLASS_NAME);
		helper.setupRelationship(ExternalTaskEntityHandler.OCLASS_NAME, "execution", ExecutionEntityHandler.OCLASS_NAME);
	}

	@Statement
	public List<ExternalTaskEntity> selectExternalTaskByQueryCriteria(OPersistenceSession session, ExternalTaskQuery query) {
		return query(session, query);
	}
	
	@Statement
	public List<ExternalTaskEntity> selectExternalTasksByExecutionId(OPersistenceSession session, ListQueryParameterObject query) {
		return queryList(session, "select from "+getSchemaClass()+" where execution.id = ?", query.getParameter());
	}
	
	@Statement
	public List<ExternalTaskEntity> selectExternalTasksForTopics(OPersistenceSession session, ListQueryParameterObject query) {
		Map<String, Object> map = (Map<String, Object>)query.getParameter();
		String sql = "select from "+getSchemaClass()+" where "
					+"(lockExpirationTime is null or lockExpirationTime <= ?) and "
					+"(suspensionState is null or suspensionState = 1) and "
					+"topicName in ? limit ?";
		return queryList(session, sql, map.get("now"), map.get("topics"), query.getMaxResults());
	}

}
