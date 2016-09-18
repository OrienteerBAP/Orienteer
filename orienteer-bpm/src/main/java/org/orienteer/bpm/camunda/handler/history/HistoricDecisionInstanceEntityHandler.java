package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.HistoricDecisionInstanceQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricDecisionInstanceEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.DecisionDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * {@link IEntityHandler} for {@link HistoricDecisionInstanceEntity}
 */
public class HistoricDecisionInstanceEntityHandler extends HistoricEventHandler<HistoricDecisionInstanceEntity> {

    public static final String OCLASS_NAME = "BPMHistoricDecisionInstance";

    public HistoricDecisionInstanceEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("decisionDefinition", OType.LINK, 10)
                .oProperty("decisionDefinitionKey", OType.STRING, 20)
                .oProperty("decisionDefinitionName", OType.STRING, 30)
                .oProperty("activityInstanceId", OType.STRING, 100)
                .oProperty("activityId", OType.STRING, 110)
                .oProperty("evaluationTime", OType.DATETIME, 120)
                .oProperty("collectResultValue", OType.DOUBLE, 130)
                .oProperty("userId", OType.STRING, 140);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);
        helper.setupRelationship(OCLASS_NAME, "decisionDefinition", DecisionDefinitionEntityHandler.OCLASS_NAME, "historyDecisionInstances");
    }

    @Statement
    public List<HistoricDecisionInstanceEntity> selectHistoricDecisionInstancesByDecisionDefinitionId(
            OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where decisionDefinition.id=?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDecisionInstanceEntity> selectHistoricDecisionInstancesByQueryCriteria(
            OPersistenceSession session, HistoricDecisionInstanceQuery query) {
        return query(session, query);
    }
}
