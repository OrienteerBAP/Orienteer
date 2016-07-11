package org.orienteer.bpm.camunda.handler;

import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.externaltask.ExternalTaskQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.ByteArrayEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExternalTaskEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
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
		helper.oProperty("topicName", OType.STRING, 10)
			  .oProperty("workerId", OType.STRING, 20)
			  .oProperty("retries", OType.INTEGER, 30)
			  .oProperty("errorMessage", OType.STRING, 40)
			  .oProperty("lockExpirationTime", OType.DATETIME, 50)
			  .oProperty("suspensionState", OType.INTEGER, 60)
			  .oProperty("executionId", OType.STRING, 70)
			  .oProperty("processInstanceId", OType.STRING, 80)
			  .oProperty("processDefinitionId", OType.STRING, 90)
			  .oProperty("processDefinitionKey", OType.STRING, 100)
			  .oProperty("activityId", OType.STRING, 110)
			  .oProperty("activityInstanceId", OType.STRING, 120)
			  .oProperty("tenantId", OType.STRING, 130)
			  .oProperty("priority", OType.LONG, 140);
	}
	
	@Statement
	public List<ExternalTaskEntity> selectExternalTaskByQueryCriteria(OPersistenceSession session, ExternalTaskQuery query) {
		return query(session, query);
	}
	
	@Statement
	public List<ExternalTaskEntity> selectExternalTasksByExecutionId(OPersistenceSession session, ListQueryParameterObject query) {
		return queryList(session, "select from "+getSchemaClass()+" where executionId = ?", query.getParameter());
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
