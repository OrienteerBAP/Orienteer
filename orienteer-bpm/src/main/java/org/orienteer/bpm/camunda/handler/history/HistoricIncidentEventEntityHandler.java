package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricIncidentEventEntity;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for {@link HistoricIncidentEventEntity}
 */
public class HistoricIncidentEventEntityHandler extends HistoricEventHandler<HistoricIncidentEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricIncidentEvent";

    public HistoricIncidentEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
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
