package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.HistoricDetail;
import org.camunda.bpm.engine.history.HistoricDetailQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricDetailEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricDetailEventEntityHandler extends AbstractEntityHandler<HistoricDetailEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricDetailEvent";

    public HistoricDetailEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("processDefintionKey", OType.STRING, 10)
                .oProperty("processDefinitionId", OType.STRING, 20)
                .oProperty("processInstanceId", OType.STRING, 30)
                .oProperty("executionId", OType.STRING, 40)
                .oProperty("caseDefinitionKey", OType.STRING, 50)
                .oProperty("caseDefintionId", OType.STRING, 60)
                .oProperty("caseInstanceId", OType.STRING, 70)
                .oProperty("caseExecutionId", OType.STRING, 80)
                .oProperty("timestamp", OType.DATETIME, 90)
                .oProperty("activityInstanceId", OType.STRING, 100)
                .oProperty("taskId", OType.STRING, 110)
                .oProperty("sequenceCounter", OType.LONG, 120)
                .oProperty("tenantId", OType.STRING, 130);
    }

    @Statement
    public List<HistoricDetailEventEntity> selectHistoricDetailsByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where processInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDetailEventEntity> selectHistoricDetailsByCaseInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDetailEventEntity> selectHistoricDetailsByTaskId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where taskId = ?", parameter.getParameter());
    }
}
