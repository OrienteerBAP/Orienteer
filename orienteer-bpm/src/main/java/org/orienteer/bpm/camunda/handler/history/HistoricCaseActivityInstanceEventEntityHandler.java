package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricCaseActivityInstanceEventEntity;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.TaskEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for {@link HistoricCaseActivityInstanceEventEntity}
 */
public class HistoricCaseActivityInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricCaseActivityInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricCaseActivityInstanceEvent";

    public HistoricCaseActivityInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("parentCaseActivityInstanceId", OType.STRING, 10)
                .oProperty("caseActivityId", OType.STRING, 40)
                .oProperty("task", OType.LINK, 50)
                .oProperty("calledProcessInstanceId", OType.STRING, 60)
                .oProperty("calledCaseInstanceId", OType.STRING, 70)
                .oProperty("caseActivityName", OType.STRING, 80)
                .oProperty("caseActivityType", OType.STRING, 90)
                .oProperty("caseActivityInstanceState", OType.INTEGER, 130)
                .oProperty("required", OType.BOOLEAN, 140);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);
        helper.setupRelationship(OCLASS_NAME, "task", TaskEntityHandler.OCLASS_NAME, "historyCaseActivityEventInstances");
    }
}
