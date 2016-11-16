package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.cmmn.entity.runtime.CaseExecutionEntity;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.runtime.CaseExecutionQuery;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.history.HistoricEventHandler;
import org.orienteer.bpm.camunda.handler.history.HistoricVariableInstanceEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by kir on 16.07.16.
 */
public class CaseExecutionEntityHandler extends AbstractEntityHandler<CaseExecutionEntity> {

    public static final String OCLASS_NAME = "BPMCaseExecution";

    public CaseExecutionEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("caseInstanceId", OType.STRING, 10)
                .oProperty("businessKey", OType.STRING, 20)
                .oProperty("caseDefinitionId", OType.STRING, 30)
                .oProperty("activityId", OType.STRING, 40)
                .oProperty("parentId", OType.STRING, 50)
                .oProperty("superCaseExecutionId", OType.STRING, 60)
                .oProperty("superExecutionId", OType.STRING, 70)
                .oProperty("state", OType.INTEGER, 80)
                .oProperty("previous", OType.INTEGER, 90)
                .oProperty("required", OType.BOOLEAN, 100)
                .oProperty("historyEvents", OType.LINKLIST, 110).assignTab("history").assignVisualization("table")
                .oProperty("historyVariableInstances", OType.LINKLIST, 120).assignVisualization("table");
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);
        helper.setupRelationship(OCLASS_NAME, "historyEvents", HistoricEventHandler.OCLASS_NAME, "caseExecution");
        helper.setupRelationship(OCLASS_NAME, "historyVariableInstances", HistoricVariableInstanceEntityHandler.OCLASS_NAME, "caseExecution");
    }

    @Statement
    public List<CaseExecutionEntity> selectCaseExecutionsByParentCaseExecutionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where parentId = ?", parameter.getParameter());
    }

    @Statement
    public List<CaseExecutionEntity> selectCaseExecutionsByCaseInstanceId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public String selectCaseInstanceIdsByCaseDefinitionId(OPersistenceSession session, final ListQueryParameterObject parameter) {
        return querySingle(session, "select id from " + getSchemaClass() + " where caseDefinitionId = ? and parentId is null",
                parameter.getParameter()).toString();
    }

    @Statement
    public List<CaseExecutionEntity> selectSubCaseInstanceBySuperCaseExecutionId(OPersistenceSession session, final String parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where superCaseExecutionId = ?", parameter);
    }

    @Statement
    public List<CaseExecutionEntity> selectSubCaseInstanceBySuperExecutionId(OPersistenceSession session, final String parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where superExecutionId = ?", parameter);
    }

    @Statement
    public List<CaseExecutionEntity> selectCaseExecutionsByQueryCriteria(OPersistenceSession session, final CaseExecutionQuery query) {
        return query(session, query);
    }

    @Statement
    public List<CaseExecutionEntity> selectCaseInstanceByQueryCriteria(OPersistenceSession session, final CaseExecutionQuery query) {
        return query(session, query);
    }
}
