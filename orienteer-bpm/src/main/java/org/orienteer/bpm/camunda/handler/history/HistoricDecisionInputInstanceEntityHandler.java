package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricDecisionInputInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.ByteArrayEntityHandler;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link HistoricDecisionInputInstanceEntity}
 */
public class HistoricDecisionInputInstanceEntityHandler extends HistoricEventHandler<HistoricDecisionInputInstanceEntity> {

    public static final String OCLASS_NAME = "BPMHistoricDecisionInputInstance";

    public HistoricDecisionInputInstanceEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("decisionInstanceId", OType.STRING, 10)
                .oProperty("clauseId", OType.STRING, 20)
                .oProperty("clauseName", OType.STRING, 30)
                .oProperty("serializerName", OType.STRING, 40)
                .oProperty("byteArray", OType.LINK, 50)
                .oProperty("doubleValue", OType.DOUBLE, 60)
                .oProperty("longValue", OType.LONG, 70)
                .oProperty("textValue", OType.STRING, 80)
                .oProperty("textValue2", OType.STRING, 90);
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
