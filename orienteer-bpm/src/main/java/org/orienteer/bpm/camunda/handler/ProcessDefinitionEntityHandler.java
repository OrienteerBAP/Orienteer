package org.orienteer.bpm.camunda.handler;


import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.OCommandSQL;

/**
 */
public class ProcessDefinitionEntityHandler extends AbstractEntityHandler<ProcessDefinitionEntity> {
	
	public static final String OCLASS_NAME = "BPMProcessDefinition";

	public ProcessDefinitionEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 10).markAsDocumentName().notNull(true)
			  .oProperty("key", OType.STRING, 30).notNull(true)
		      .oProperty("resourceName", OType.STRING, 35)
			  .oProperty("category", OType.STRING, 40)
			  .oProperty("deploymentId", OType.STRING, 60)
			  .oProperty("suspensionState", OType.INTEGER, 70).defaultValue("1").notNull();
	}
	
	@Statement
	public List<ProcessDefinitionEntity> selectLatestProcessDefinitionByKey(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where key = ? order by @rid desc limit 1", param.getParameter());
	}
	
	@Statement
	public ProcessDefinitionEntity selectLatestProcessDefinitionByKeyWithoutTenantId(OPersistenceSession session, Map<String, Object> params) {
		return querySingle(session, "select from "+getSchemaClass()+" where key = ? order by @rid desc", params.get("processDefinitionKey"));
	}
	
	@Statement
	public List<ProcessDefinitionEntity> selectProcessDefinitionByDeploymentId(OPersistenceSession session, ListQueryParameterObject param) {
		return queryList(session, "select from "+getSchemaClass()+" where deploymentId = ?", param.getParameter());
	}
	
	@Statement
	public List<ProcessDefinitionEntity> selectProcessDefinitionByKeyIn(OPersistenceSession session, ListQueryParameterObject params) {
		String[] keys = (String[]) params.getParameter();
		return queryList(session, "select from "+getSchemaClass()+" where key in ?", (Object)keys);
	}
	
	@Statement
	public List<ProcessDefinitionEntity> selectProcessDefinitionsByQueryCriteria(OPersistenceSession session, ProcessDefinitionQuery query) {
		return query(session, query);
	}
	
	@Statement
	public ProcessDefinitionEntity selectProcessDefinitionByDeploymentAndKey(OPersistenceSession session, Map<String, Object> map) {
		return querySingle(session, "select from "+getSchemaClass()+" where deploymentId = ? and key = ?", map.get("deploymentId"), map.get("processDefinitionKey"));
	}
	
	@Statement
	public void deleteProcessDefinitionsByDeploymentId(OPersistenceSession session, String deploymentId) {
		session.getDatabase().command(new OCommandSQL("delete from "+getSchemaClass()+" where deploymentId = ?"))
									.execute(deploymentId);
	}

}
