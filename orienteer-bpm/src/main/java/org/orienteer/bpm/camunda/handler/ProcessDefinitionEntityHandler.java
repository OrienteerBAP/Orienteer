package org.orienteer.bpm.camunda.handler;


import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.deploy.DeploymentCache;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.orienteer.bpm.camunda.BpmnHook;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.OProcessEngineConfiguration;
import org.orienteer.bpm.camunda.handler.history.HistoricEventHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricProcessInstanceEventEntityHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricVariableInstanceEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.hook.ORecordHook.TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link IEntityHandler} for {@link ProcessDefinitionEntity}es
 */
public class ProcessDefinitionEntityHandler extends AbstractEntityHandler<ProcessDefinitionEntity> {
	
	public static final String OCLASS_NAME = "BPMProcessDefinition";

	public ProcessDefinitionEntityHandler() {
		super(OCLASS_NAME);
	}
	
	@Override
	public void applySchema(OSchemaHelper helper) {
		super.applySchema(helper);
		helper.oProperty("name", OType.STRING, 10).markDisplayable().markAsDocumentName().notNull(true)
			  .oProperty("key", OType.STRING, 30).notNull(true).markDisplayable()
		      .oProperty("resourceName", OType.STRING, 35)
			  .oProperty("category", OType.STRING, 40).markDisplayable()
			  .oProperty("deployment", OType.LINK, 60).assignVisualization("listbox").markDisplayable().markAsLinkToParent()
			  .oProperty("executions", OType.LINKLIST, 70).assignVisualization("table")
			  .oProperty("suspensionState", OType.INTEGER, 80).defaultValue("1").notNull()
		      .oProperty("tasks", OType.LINKLIST, 90).assignVisualization("table")
			  .oProperty("historyEvents", OType.LINKLIST, 100).assignTab("history").assignVisualization("table")
			  .oProperty("historyVariableInstances", OType.LINKLIST, 110).assignTab("history").assignVisualization("table");
	}
	
	@Override
	public void applyRelationships(OSchemaHelper helper) {
		super.applyRelationships(helper);
		helper.setupRelationship(OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME, "processDefinitions");
		helper.setupRelationship(OCLASS_NAME, "executions", ExecutionEntityHandler.OCLASS_NAME, "processDefinition");
		helper.setupRelationship(OCLASS_NAME, "tasks", TaskEntityHandler.OCLASS_NAME, "processDefinition");
		helper.setupRelationship(OCLASS_NAME, "historyEvents", HistoricEventHandler.OCLASS_NAME, "processDefinition");
		helper.setupRelationship(OCLASS_NAME, "historyVariableInstances", HistoricVariableInstanceEntityHandler.OCLASS_NAME, "processDefinition");
	}
	
	@Override
	public RESULT onTrigger(ODatabaseDocument db, ODocument doc, TYPE iType) {
		if(iType.equals(TYPE.BEFORE_CREATE)) {
			ODocument deployment = doc.field("deployment");
			if(deployment==null) {
				List<ODocument> deployments = db.query(new OSQLSynchQuery<>("select from "+DeploymentEntityHandler.OCLASS_NAME, 1));
				deployment = deployments!=null && !deployments.isEmpty()?deployments.get(0):null;
				if(deployment==null) {
					deployment = new ODocument(DeploymentEntityHandler.OCLASS_NAME);
					deployment.field("id", BpmnHook.getNextId());
					deployment.field("name", "Orienteer");
					deployment.save();
				}
				doc.field("deployment", deployment);
				return RESULT.RECORD_CHANGED;
			}
		} else if(iType.equals(TYPE.AFTER_UPDATE) || iType.equals(TYPE.AFTER_DELETE)) {
			DeploymentCache dc = OProcessEngineConfiguration.get().getDeploymentCache();
			dc.removeProcessDefinition((String) doc.field("id"));
		}
		
		return RESULT.RECORD_NOT_CHANGED; 
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
		return queryList(session, "select from "+getSchemaClass()+" where deployment.id = ?", param.getParameter());
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
		return querySingle(session, "select from "+getSchemaClass()+" where deployment.id = ? and key = ?", map.get("deploymentId"), map.get("processDefinitionKey"));
	}
	
	@Statement
	public void deleteProcessDefinitionsByDeploymentId(OPersistenceSession session, String deploymentId) {
		session.getDatabase().command(new OCommandSQL("delete from "+getSchemaClass()+" where deployment.id = ?"))
									.execute(deploymentId);
	}

}
