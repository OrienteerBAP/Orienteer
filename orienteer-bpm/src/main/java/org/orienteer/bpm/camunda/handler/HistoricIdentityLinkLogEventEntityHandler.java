package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricIdentityLinkLogEventEntity;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricIdentityLinkLogEventEntityHandler extends AbstractEntityHandler<HistoricIdentityLinkLogEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricIdentityLinkLogEvent";

    public HistoricIdentityLinkLogEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("time", OType.DATETIME, 10)
                .oProperty("type", OType.STRING, 20)
                .oProperty("userId", OType.STRING, 30)
                .oProperty("groupId", OType.STRING, 40)
                .oProperty("taskId", OType.STRING, 50)
                .oProperty("processDefinitionId", OType.STRING, 60)
                .oProperty("operationType", OType.STRING, 70)
                .oProperty("assignerId", OType.STRING, 80)
                .oProperty("processDefinitionKey", OType.STRING, 90)
                .oProperty("tenantId", OType.STRING, 100);
    }
}
