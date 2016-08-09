package org.orienteer.bpm.camunda.handler.historic;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricScopeInstanceEvent;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 09.08.16.
 */
public class HistoricScopeInstanceEventHandler<T extends HistoricScopeInstanceEvent> extends HistoricEventHandler<T> {

    public static final String OCLASS_NAME = "BPMHistoricScopeInstanceEventHandler";

    public HistoricScopeInstanceEventHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("durationInMillis", OType.LONG, 10)
                .oProperty("startTime", OType.DATETIME, 20)
                .oProperty("endTime", OType.DATETIME, 30);
    }
}
