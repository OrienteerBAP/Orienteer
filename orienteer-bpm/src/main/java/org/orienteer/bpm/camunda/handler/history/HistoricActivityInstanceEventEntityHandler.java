package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.NonUniqIdConverter;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.bpm.camunda.handler.TaskEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link HistoricActivityInstanceEventEntity}
 */
public class HistoricActivityInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricActivityInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricActivityInstanceEvent";

    public HistoricActivityInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("activityId", OType.STRING, 10)
                .oProperty("activityName", OType.STRING, 20)
                .oProperty("activityType", OType.STRING, 30)
                .oProperty("activityInstanceId", OType.STRING, 40)
                .oProperty("activityInstanceState", OType.INTEGER, 50)
                .oProperty("parentActivityInstanceId", OType.STRING, 60)
                .oProperty("calledProcessInstanceId", OType.STRING, 70)
                .oProperty("calledCaseInstanceId", OType.STRING, 80)
                .oProperty("task", OType.LINK, 90)
                .oProperty("taskAssignee", OType.STRING, 100);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);
        helper.setupRelationship(OCLASS_NAME, "task", TaskEntityHandler.OCLASS_NAME, "historyActivityInstances");
    }

    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingConvertors.put("id", new NonUniqIdConverter("a:"));
    }

    @Statement
    public void deleteHistoricActivityInstancesByProcessInstanceId(OPersistenceSession session, String processInstanceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("processInstanceId", processInstanceId);

        delete(session, params);
    }
}
