package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.HistoricVariableInstanceQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.*;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * {@link IEntityHandler} for {@link HistoricVariableInstanceEntity}
 */
public class HistoricVariableInstanceEntityHandler extends AbstractEntityHandler<HistoricVariableInstanceEntity> {

    public static final String OCLASS_NAME = "BPMHistoricVariableInstance";

    public HistoricVariableInstanceEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.domain(OClassDomain.SYSTEM);
        helper.oProperty("processDefinitionKey", OType.STRING, 10)
                .oProperty("processDefinition", OType.LINK, 20)
                .oProperty("processInstance", OType.LINK, 30)
                .oProperty("execution", OType.LINK, 40)
                .oProperty("activityInstanceId", OType.STRING, 50)
                .oProperty("caseDefinitionKey", OType.STRING, 70)
                .oProperty("caseDefinition", OType.LINK, 80)
                .oProperty("caseInstanceId", OType.STRING, 90)
                .oProperty("caseExecution", OType.LINK, 100)
                .oProperty("task", OType.LINK, 110)
                .oProperty("variableName", OType.STRING, 120)
                .oProperty("serializerName", OType.STRING, 140)
                .oProperty("byteArray", OType.LINK, 150)
                .oProperty("doubleValue", OType.DOUBLE, 160)
                .oProperty("longValue", OType.LONG, 170)
                .oProperty("textValue", OType.STRING, 180)
                .oProperty("textValue2", OType.STRING, 190);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);

        helper.setupRelationship(OCLASS_NAME, "processInstance", ExecutionEntityHandler.OCLASS_NAME, "historyVariableInstances");
        helper.setupRelationship(OCLASS_NAME, "execution", ExecutionEntityHandler.OCLASS_NAME);
        helper.setupRelationship(OCLASS_NAME, "processDefinition", ProcessDefinitionEntityHandler.OCLASS_NAME, "historyVariableInstances");
        helper.setupRelationship(OCLASS_NAME, "caseDefinition", CaseDefinitionEntityHandler.OCLASS_NAME, "historyVariableInstances");
        helper.setupRelationship(OCLASS_NAME, "caseExecution", CaseExecutionEntityHandler.OCLASS_NAME, "historyVariableInstances");
        helper.setupRelationship(OCLASS_NAME, "task", TaskEntityHandler.OCLASS_NAME, "historyVariableInstances");
        helper.setupRelationship(OCLASS_NAME, "byteArray", ByteArrayEntityHandler.OCLASS_NAME);
    }

    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingConvertors.put("id", new NonUniqIdConverter("var:"));
    }

    @Statement
    public List<HistoricVariableInstanceEntity> selectHistoricVariablesByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where processInstance.id = ?", parameter.getParameter());
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
