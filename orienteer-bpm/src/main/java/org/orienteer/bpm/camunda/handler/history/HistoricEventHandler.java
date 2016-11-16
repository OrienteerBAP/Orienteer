package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.orienteer.bpm.camunda.handler.*;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for {@link HistoryEvent}
 * @param <T> - type of {@link HistoryEvent}
 */
public class HistoricEventHandler<T extends HistoryEvent> extends AbstractEntityHandler<T> {

    public static final String OCLASS_NAME = "BPMHistoryEvent";

    public HistoricEventHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {

        helper.oClass(OCLASS_NAME, BPM_ENTITY_CLASS)
        		.oProperty("processInstance", OType.LINK, 10).markAsLinkToParent()
                .oProperty("execution", OType.LINK, 20)
                .oProperty("processDefinition", OType.LINK, 30)
                .oProperty("processDefinitionKey", OType.STRING, 40)
                .oProperty("caseInstanceId", OType.STRING, 50)
                .oProperty("caseExecution", OType.LINK, 60)
                .oProperty("caseDefinition", OType.LINK, 70)
                .oProperty("caseDefinitionKey", OType.STRING, 80)
                .oProperty("eventType", OType.STRING, 90).markAsDocumentName()
                .oProperty("sequenceCounter", OType.LONG, 100);
    }
    
    @Override
    public void applyRelationships(OSchemaHelper helper) {
    	helper.setupRelationship(OCLASS_NAME, "processInstance", ExecutionEntityHandler.OCLASS_NAME, "historyEvents");
    	helper.setupRelationship(OCLASS_NAME, "execution", ExecutionEntityHandler.OCLASS_NAME);
    	helper.setupRelationship(OCLASS_NAME, "processDefinition", ProcessDefinitionEntityHandler.OCLASS_NAME, "historyEvents");
        helper.setupRelationship(OCLASS_NAME, "caseDefinition", CaseDefinitionEntityHandler.OCLASS_NAME, "historyEvents");
        helper.setupRelationship(OCLASS_NAME, "caseExecution", CaseExecutionEntityHandler.OCLASS_NAME, "historyEvents");
    }
}
