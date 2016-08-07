package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricCaseActivityInstanceEventEntity;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricCaseActivityInstanceEventEntityHandler extends AbstractEntityHandler<HistoricCaseActivityInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricCaseActivityInstanceEvent";

    public HistoricCaseActivityInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("parentCaseActivityInstanceId", OType.STRING, 10)
                .oProperty("caseDefinitionId", OType.STRING, 20)
                .oProperty("caseInstanceId", OType.STRING, 30)
                .oProperty("caseActivityId", OType.STRING, 40)
                .oProperty("taskId", OType.STRING, 50)
                .oProperty("calledProcessInstanceId", OType.STRING, 60)
                .oProperty("calledCaseInstanceId", OType.STRING, 70)
                .oProperty("caseActivityName", OType.STRING, 80)
                .oProperty("caseActivityType", OType.STRING, 90)
                .oProperty("startTime", OType.DATETIME, 100)
                .oProperty("endTime", OType.DATETIME, 110)
                .oProperty("durationInMillis", OType.LONG, 120)
                .oProperty("caseActivityInstanceState", OType.INTEGER, 130)
                .oProperty("required", OType.BOOLEAN, 140)
                .oProperty("tenantId", OType.STRING, 150);
    }
}
