package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.HistoricVariableInstanceQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by kir on 06.08.16.
 */
public class HistoricVariableInstanceEntityHandler extends AbstractEntityHandler<HistoricVariableInstanceEntity> {

    public static final String OCLASS_NAME = "BPMHistoricVariableInstance";

    public HistoricVariableInstanceEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("processDefinitionKey", OType.STRING, 10)
                .oProperty("processDefinitionId", OType.STRING, 20)
                .oProperty("processInstanceId", OType.STRING, 30)
                .oProperty("executionId", OType.STRING, 40)
                .oProperty("activityInstanceId", OType.STRING, 50)
                .oProperty("tenantId", OType.STRING, 60)
                .oProperty("caseDefinitionKey", OType.STRING, 70)
                .oProperty("caseDefinitionId", OType.STRING, 80)
                .oProperty("caseInstanceId", OType.STRING, 90)
                .oProperty("caseExecutionId", OType.STRING, 100)
                .oProperty("taskId", OType.STRING, 110)
                .oProperty("variableName", OType.STRING, 120)
                .oProperty("revision", OType.STRING, 130)
                .oProperty("serializerName", OType.STRING, 140)
                .oProperty("byteArrayId", OType.STRING, 150)
                .oProperty("doubleValue", OType.DOUBLE, 160)
                .oProperty("longValue", OType.LONG, 170)
                .oProperty("textValue", OType.STRING, 180)
                .oProperty("textValue2", OType.STRING, 190);
    }
    
    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingConvertors.put("id", new NonUniqIdConverter("var:"));
    }

    @Statement
    public List<HistoricVariableInstanceEntity> selectHistoricVariablesByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where processInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public List<HistoricVariableInstanceEntity> selectHistoricVariablesByCaseInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public List<HistoricVariableInstanceEntity> selectHistoricVariableInstanceByQueryCriteria(OPersistenceSession session, HistoricVariableInstanceQuery query) {
        return query(session, query);
    }
}
