package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricIncidentEventEntity;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.JobDefinitionEntityHandler;
import org.orienteer.core.OClassDomain;
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
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("createTime", OType.DATETIME, 40)
                .oProperty("endTime", OType.DATETIME, 50)
                .oProperty("incidentMessage", OType.STRING, 60)
                .oProperty("incidentType", OType.STRING, 70)
                .oProperty("activityId", OType.STRING, 80)
                .oProperty("causeIncidentId", OType.STRING, 90)
                .oProperty("rootCauseIncidentId", OType.STRING, 100)
                .oProperty("configuration", OType.STRING, 110)
                .oProperty("incidentState", OType.INTEGER, 120)
                .oProperty("jobDefinition", OType.LINK, 140);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);

        helper.setupRelationship(OCLASS_NAME, "jobDefinition", JobDefinitionEntityHandler.OCLASS_NAME, "historyIncidentEvents");
    }
}
