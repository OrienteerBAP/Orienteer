package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricScopeInstanceEvent;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for child of {@link HistoricScopeInstanceEvent}
 * @param <T> the type of {@link HistoricScopeInstanceEvent}
 */
public class HistoricScopeInstanceEventHandler<T extends HistoricScopeInstanceEvent> extends HistoricEventHandler<T> {

    public static final String OCLASS_NAME = "BPMHistoricScopeInstanceEventHandler";

    public HistoricScopeInstanceEventHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("durationInMillis", OType.LONG, 110)
                .oProperty("startTime", OType.DATETIME, 120)
                .oProperty("endTime", OType.DATETIME, 130);
    }
}
