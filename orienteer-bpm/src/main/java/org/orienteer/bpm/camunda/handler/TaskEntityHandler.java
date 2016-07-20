package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.DeploymentEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.task.TaskQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * {@link IEntityHandler} for {@link TaskEntity} 
 */
public class TaskEntityHandler extends AbstractEntityHandler<TaskEntity> {

	public static final String OCLASS_NAME = "BPMTask";
	
    public TaskEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.oProperty("name", OType.STRING, 20).markAsDocumentName().markDisplayable()
                .oProperty("parentTask", OType.LINK, 30).assignVisualization("listbox")
                .oProperty("childTasks", OType.LINKLIST, 31).assignVisualization("table")
                .oProperty("description", OType.STRING, 40).markDisplayable()
                .oProperty("priority", OType.INTEGER, 50).markDisplayable()
                .oProperty("createTime", OType.DATETIME, 60).markDisplayable()
                .oProperty("owner", OType.STRING, 70).markDisplayable()
                .oProperty("assignee", OType.LINK, 80).markDisplayable()
                .oProperty("delegationStateString", OType.STRING, 90)
                .oProperty("execution", OType.LINK, 100).assignVisualization("listbox")
                .oProperty("processInstanceId", OType.STRING, 110).markAsLinkToParent().markDisplayable()
                .oProperty("processDefinition", OType.LINK, 120)
                .oProperty("caseExecutionId", OType.STRING, 130)
                .oProperty("caseInstanceId", OType.STRING, 140)
                .oProperty("caseDefinitionId", OType.STRING, 150)
                .oProperty("taskDefinitionKey", OType.STRING, 160)
                .oProperty("dueDate", OType.DATETIME, 170).markDisplayable()
                .oProperty("followUpDate", OType.DATETIME, 180).markDisplayable()
                .oProperty("suspensionState", OType.INTEGER, 190)
                .oProperty("variables", OType.LINKLIST, 200).assignVisualization("table");
//                .oProperty("tenantId", OType.STRING, 200); // Tenants are not supported
    }
    
    @Override
    public void applyRelationships(OSchemaHelper helper) {
    	super.applyRelationships(helper);
    	helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "assignee", UserEntityHandler.OCLASS_NAME, "assignedTasks");
        helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "processDefinition", ProcessDefinitionEntityHandler.OCLASS_NAME, "tasks");
        helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "parentTask", TaskEntityHandler.OCLASS_NAME, "childTasks");
        helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "childTasks", TaskEntityHandler.OCLASS_NAME, "parentTask");
        helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "execution", ExecutionEntityHandler.OCLASS_NAME, "tasks");
        helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "variables", VariableInstanceEntityHandler.OCLASS_NAME, "task");
    }
    
    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingFromDocToEntity.keySet().removeAll(Arrays.asList("name", "parentTask.id", "description",
    															"priority", "owner", "assignee", 
    															"caseInstanceId", "taskDefinitionKey", 
    															"dueDate", "followUpDate"));
    	mappingFromEntityToDoc.put("assignee", "assignee.name");
    }
    
    @Override
    public TaskEntity mapToEntity(ODocument doc, TaskEntity entity, OPersistenceSession session) {
    	TaskEntity ret =  super.mapToEntity(doc, entity, session);
    	ret.setNameWithoutCascade((String)doc.field("name"));
    	ret.setParentTaskIdWithoutCascade((String)doc.field("parentTask.id"));
    	ret.setDescriptionWithoutCascade((String)doc.field("description"));
    	ret.setPriorityWithoutCascade((Integer)doc.field("priority"));
    	ret.setOwnerWithoutCascade((String)doc.field("owner"));
    	ret.setAssigneeWithoutCascade((String)doc.field("assignee.name"));
    	ret.setCaseInstanceIdWithoutCascade((String)doc.field("caseInstanceId"));
    	ret.setTaskDefinitionKeyWithoutCascade((String)doc.field("taskDefinitionKey"));
    	ret.setDueDateWithoutCascade((Date)doc.field("dueDate"));
    	ret.setFollowUpDateWithoutCascade((Date)doc.field("followUpDate"));
    	return ret;
    }

    @Statement
    public List<TaskEntity> selectTasksByParentTaskId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where parentTask.id=?", parameter.getParameter());
    }

    @Statement
    public List<TaskEntity> selectTasksByExecutionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where execution.id=?", parameter.getParameter());
    }

    @Statement
    public List<TaskEntity> selectTasksByProcessInstanceId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where proccessInstanceId=?", parameter.getParameter());
    }

    @Statement
    public List<TaskEntity> selectTaskByCaseExecutionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseExecutionId=?", parameter.getParameter());
    }

    @Statement
    public List<TaskEntity> selectTaskByQueryCriteria(OPersistenceSession session, TaskQuery query) {
        return query(session, query);
    }
}