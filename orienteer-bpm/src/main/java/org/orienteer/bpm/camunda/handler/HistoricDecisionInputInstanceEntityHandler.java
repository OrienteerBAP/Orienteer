package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricDecisionInputInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.subentity.HistoricEventHandler;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricDecisionInputInstanceEntityHandler extends HistoricEventHandler<HistoricDecisionInputInstanceEntity> {

    public static final String OCLASS_NAME = "BPMHistoricDecisionInputInstance";

    public HistoricDecisionInputInstanceEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("decisionInstanceId", OType.STRING, 10)
                .oProperty("clauseId", OType.STRING, 20)
                .oProperty("clauseName", OType.STRING, 30)
                .oProperty("serializerName", OType.STRING, 40)
                .oProperty("byteArrayValueId", OType.STRING, 50)
                .oProperty("doubleValue", OType.DOUBLE, 60)
                .oProperty("longValue", OType.LONG, 70)
                .oProperty("textValue", OType.STRING, 80)
                .oProperty("textValue2", OType.STRING, 90)
                .oProperty("tenantId", OType.STRING, 100);
    }

    @Statement
    public List<HistoricDecisionInputInstanceEntity> selectHistoricDecisionInputInstancesByDecisionInstanceId(
            OPersistenceSession session, final ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where decisionInstanceId=?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDecisionInputInstanceEntity> selectHistoricDecisionInputInstancesByDecisionInstanceIds(
            OPersistenceSession session, Map<String, ?> parameters) {
        return query(session, parameters);
    }
}
