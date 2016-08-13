package org.orienteer.bpm.camunda.handler.subentity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.orienteer.bpm.camunda.handler.AbstractEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by KMukhov on 09.08.16.
 */
public class HistoricEventHandler<T extends HistoryEvent> extends AbstractEntityHandler<T> {

    public static final String OCLASS_NAME = "BPMHistoryEvent";

    public HistoricEventHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("processInstanceId", OType.STRING, 10)
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
