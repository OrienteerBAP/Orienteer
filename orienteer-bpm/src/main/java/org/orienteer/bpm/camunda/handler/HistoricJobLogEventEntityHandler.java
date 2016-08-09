package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricJobLogEventEntity;
import org.orienteer.bpm.camunda.handler.historic.HistoricEventHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricJobLogEventEntityHandler extends HistoricEventHandler<HistoricJobLogEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricJobLogEvent";

    public HistoricJobLogEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("timestamp", OType.DATETIME, 10)
                .oProperty("jobId", OType.STRING, 20)
                .oProperty("jobDueDate", OType.DATETIME, 30)
                .oProperty("jobRetries", OType.INTEGER, 40)
                .oProperty("jobPriority", OType.LONG, 50)
                .oProperty("jobExceptionMessage", OType.STRING, 60)
                .oProperty("exceptionByteArrayId", OType.STRING, 70)
                .oProperty("state", OType.INTEGER, 80)
                .oProperty("jobDefinitionId", OType.STRING, 90)
                .oProperty("jobDefinitionType", OType.STRING, 100)
                .oProperty("jobDefinitionConfiguration", OType.STRING, 110)
                .oProperty("activityId", OType.STRING, 120)
                .oProperty("deploymentId", OType.STRING, 170)
                .oProperty("tenantId", OType.STRING, 180);
    }
}
