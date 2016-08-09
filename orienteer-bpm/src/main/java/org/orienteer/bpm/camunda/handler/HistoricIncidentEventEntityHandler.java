package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricIncidentEventEntity;
import org.orienteer.bpm.camunda.handler.historic.HistoricEventHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricIncidentEventEntityHandler extends HistoricEventHandler<HistoricIncidentEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricIncidentEvent";

    public HistoricIncidentEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("createTime", OType.DATETIME, 40)
                .oProperty("endTime", OType.DATETIME, 50)
                .oProperty("incidentMessage", OType.STRING, 60)
                .oProperty("incidentType", OType.STRING, 70)
                .oProperty("activityId", OType.STRING, 80)
                .oProperty("causeIncidentId", OType.STRING, 90)
                .oProperty("rootCauseIncidentId", OType.STRING, 100)
                .oProperty("configuration", OType.STRING, 110)
                .oProperty("incidentState", OType.INTEGER, 120)
                .oProperty("tenantId", OType.STRING, 130)
                .oProperty("jobDefinitionId", OType.STRING, 140);
    }
}
