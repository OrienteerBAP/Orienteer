package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricDecisionOutputInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.ByteArrayEntityHandler;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link HistoricDecisionOutputInstanceEntity}
 */
public class HistoricDecisionOutputInstanceEntityHandler extends HistoricEventHandler<HistoricDecisionOutputInstanceEntity> {

    public static final String OCLASS_NAME = "BPMHistoricDecisionOutputInstance";

    public HistoricDecisionOutputInstanceEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("decisionInstanceId", OType.STRING, 10)
                .oProperty("clauseId", OType.STRING, 20)
                .oProperty("clauseName", OType.STRING, 30)
                .oProperty("ruleId", OType.STRING, 40)
                .oProperty("ruleOrder", OType.STRING, 50)
                .oProperty("variableName", OType.STRING, 60)
                .oProperty("serializerName", OType.STRING, 70)
                .oProperty("byteArray", OType.LINK, 80)
                .oProperty("doubleValue", OType.DOUBLE, 90)
                .oProperty("longValue", OType.LONG, 100)
                .oProperty("textValue", OType.STRING, 110)
                .oProperty("textValue2", OType.STRING, 120);
    }

    @Statement
    public List<HistoricDecisionOutputInstanceEntity> selectHistoricDecisionOutputInstancesByDecisionInstanceId(
            OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where decisionInstanceId=?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDecisionOutputInstanceEntity> selectHistoricDecisionOutputInstancesByDecisionInstanceIds(
            OPersistenceSession session, Map<String, ?> parameters) {
        return query(session, parameters);
    }
}
