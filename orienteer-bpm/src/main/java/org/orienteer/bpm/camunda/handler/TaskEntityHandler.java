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
        helper.oProperty("name", OType.STRING, 20)
                .oProperty("parentTaskId", OType.STRING, 30)
                .oProperty("description", OType.STRING, 40)
                .oProperty("priority", OType.INTEGER, 50)
                .oProperty("createTime", OType.DATETIME, 60)
                .oProperty("owner", OType.STRING, 70)
                .oProperty("assignee", OType.LINK, 80)
                .oProperty("delegationStateString", OType.STRING, 90)
                .oProperty("executionId", OType.STRING, 100)
                .oProperty("processInstanceId", OType.STRING, 110)
                .oProperty("processDefinitionId", OType.STRING, 120)
                .oProperty("caseExecutionId", OType.STRING, 130)
                .oProperty("caseInstanceId", OType.STRING, 140)
                .oProperty("caseDefinitionId", OType.STRING, 150)
                .oProperty("taskDefinitionKey", OType.STRING, 160)
                .oProperty("dueDate", OType.DATETIME, 170)
                .oProperty("followUpDate", OType.DATETIME, 180)
                .oProperty("suspensionState", OType.INTEGER, 190);
//                .oProperty("tenantId", OType.STRING, 200); // Tenants are not supported
    }
    
    @Override
    public void applyRelationships(OSchemaHelper helper) {
    	super.applyRelationships(helper);
    	helper.setupRelationship(TaskEntityHandler.OCLASS_NAME, "assignee", UserEntityHandler.OCLASS_NAME, "assignedTasks");
    }
    
    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingFromDocToEntity.keySet().removeAll(Arrays.asList("name", "parentTaskId", "description", 
    															"priority", "owner", "assignee", 
    															"caseInstanceId", "taskDefinitionKey", 
    															"dueDate", "followUpDate"));
    	mappingFromEntityToDoc.put("assignee", "assignee.name");
    }
    
    @Override
    public TaskEntity mapToEntity(ODocument doc, TaskEntity entity, OPersistenceSession session) {
    	TaskEntity ret =  super.mapToEntity(doc, entity, session);
    	ret.setNameWithoutCascade((String)doc.field("name"));
    	ret.setParentTaskIdWithoutCascade((String)doc.field("parentTaskId"));
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
        return queryList(session, "select from " + getSchemaClass() + " where parentTaskId=?", parameter.getParameter());
    }

    @Statement
    public List<TaskEntity> selectTasksByExecutionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where executionId=?", parameter.getParameter());
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