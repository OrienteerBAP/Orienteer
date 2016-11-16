package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricIdentityLinkLogEventEntity;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.UserEntityHandler;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for {@link HistoricIdentityLinkLogEventEntity}
 */
public class HistoricIdentityLinkLogEventEntityHandler extends HistoricEventHandler<HistoricIdentityLinkLogEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricIdentityLinkLogEvent";

    public HistoricIdentityLinkLogEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("time", OType.DATETIME, 10)
                .oProperty("type", OType.STRING, 20)
                .oProperty("user", OType.LINK, 30)
                .oProperty("groupId", OType.STRING, 40)
                .oProperty("taskId", OType.STRING, 50)
                .oProperty("operationType", OType.STRING, 70)
                .oProperty("assignerId", OType.STRING, 80);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);

        helper.setupRelationship(OCLASS_NAME, "user", UserEntityHandler.OCLASS_NAME);
    }
}
