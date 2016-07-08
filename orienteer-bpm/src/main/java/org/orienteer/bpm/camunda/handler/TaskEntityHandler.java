package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;
import org.camunda.bpm.engine.task.TaskQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

public class TaskEntityHandler extends AbstractEntityHandler<TaskEntity> {

    public TaskEntityHandler() {
        super("BPMTask");
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.oProperty("id", OType.STRING, 10)
                .oProperty("name", OType.STRING, 20)
                .oProperty("parentTaskId", OType.STRING, 30)
                .oProperty("description", OType.STRING, 40)
                .oProperty("priority", OType.INTEGER, 50)
                .oProperty("createTime", OType.DATETIME, 60)
                .oProperty("owner", OType.STRING, 70)
                .oProperty("assignee", OType.STRING, 80)
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
                .oProperty("suspensionState", OType.INTEGER, 190)
                .oProperty("tenantId", OType.STRING, 200);
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
    public List<TaskEntity> selectTasksByCaseExecutionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseExecutionId=?", parameter.getParameter());
    }

    @Statement
    public List<TaskEntity> selectTasksByQueryCriteria(OPersistenceSession session, TaskQuery query) {
        return query(session, query);
    }
}