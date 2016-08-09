package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricCaseActivityInstanceEventEntity;
import org.orienteer.bpm.camunda.handler.historic.HistoricScopeInstanceEventHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricCaseActivityInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricCaseActivityInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricCaseActivityInstanceEvent";

    public HistoricCaseActivityInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("parentCaseActivityInstanceId", OType.STRING, 10)
                .oProperty("caseActivityId", OType.STRING, 40)
                .oProperty("taskId", OType.STRING, 50)
                .oProperty("calledProcessInstanceId", OType.STRING, 60)
                .oProperty("calledCaseInstanceId", OType.STRING, 70)
                .oProperty("caseActivityName", OType.STRING, 80)
                .oProperty("caseActivityType", OType.STRING, 90)
                .oProperty("caseActivityInstanceState", OType.INTEGER, 130)
                .oProperty("required", OType.BOOLEAN, 140)
                .oProperty("tenantId", OType.STRING, 150);
    }
}
