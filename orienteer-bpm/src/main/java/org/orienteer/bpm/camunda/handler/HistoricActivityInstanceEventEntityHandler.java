package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.orienteer.core.util.OSchemaHelper;


/**
 * Created by kir on 06.08.16.
 */
public class HistoricActivityInstanceEventEntityHandler extends AbstractEntityHandler<HistoricActivityInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricActivityInstanceEvent";

    public HistoricActivityInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("parentActivityInstanceId", OType.STRING, 10)
                .oProperty("processDefinitionKey", OType.STRING, 20)
                .oProperty("processDefinitionId", OType.STRING, 30)
                .oProperty("processInstanceId", OType.STRING, 40)
                .oProperty("executionId", OType.STRING, 50)
                .oProperty("activityId", OType.STRING, 60)
                .oProperty("taskId", OType.STRING, 70)
                .oProperty("calledProcessInstanceId", OType.STRING, 80)
                .oProperty("calledCaseInstanceId", OType.STRING, 90)
                .oProperty("activityName", OType.STRING, 100)
                .oProperty("activityType", OType.STRING, 110)
                .oProperty("taskAssignee", OType.STRING, 120)
                .oProperty("startTime", OType.DATETIME, 130)
                .oProperty("endTime", OType.DATETIME, 140)
                .oProperty("durationInMillis", OType.LONG, 150)
                .oProperty("activityInstanceState", OType.INTEGER, 160)
                .oProperty("sequenceCounter", OType.LONG, 170)
                .oProperty("tenantId", OType.STRING, 180);
    }
}
