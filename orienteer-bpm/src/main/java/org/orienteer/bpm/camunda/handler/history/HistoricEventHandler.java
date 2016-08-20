package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.orienteer.bpm.camunda.handler.AbstractEntityHandler;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for {@link HistoryEvent}
 */
public class HistoricEventHandler<T extends HistoryEvent> extends AbstractEntityHandler<T> {

    public static final String OCLASS_NAME = "BPMHistoryEvent";

    public HistoricEventHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {

        helper.oClass(OCLASS_NAME, BPM_ENTITY_CLASS)
        		.oProperty("processInstanceId", OType.STRING, 10)
                .oProperty("executionId", OType.STRING, 20)
                .oProperty("processDefinitionId", OType.STRING, 30)
                .oProperty("processDefinitionKey", OType.STRING, 40)
                .oProperty("caseInstanceId", OType.STRING, 50)
                .oProperty("caseExecutionId", OType.STRING, 60)
                .oProperty("caseDefinitionId", OType.STRING, 70)
                .oProperty("caseDefinitionKey", OType.STRING, 80)
                .oProperty("eventType", OType.STRING, 90)
                .oProperty("sequenceCounter", OType.LONG, 100);
    }
}
