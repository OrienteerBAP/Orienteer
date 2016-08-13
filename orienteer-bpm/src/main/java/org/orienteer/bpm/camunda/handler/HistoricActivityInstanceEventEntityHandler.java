package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.subentity.HistoricScopeInstanceEventHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * Created by kir on 06.08.16.
 */
public class HistoricActivityInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricActivityInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricActivityInstanceEvent";

    public HistoricActivityInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("activityId", OType.STRING, 10)
                .oProperty("activityName", OType.STRING, 20)
                .oProperty("activityType", OType.STRING, 30)
                .oProperty("activityInstanceId", OType.STRING, 40)
                .oProperty("activityInstanceState", OType.INTEGER, 50)
                .oProperty("parentActivityInstanceId", OType.STRING, 60)
                .oProperty("calledProcessInstanceId", OType.STRING, 70)
                .oProperty("calledCaseInstanceId", OType.STRING, 80)
                .oProperty("taskId", OType.STRING, 90)
                .oProperty("taskAssignee", OType.STRING, 100)
                .oProperty("tenantId", OType.STRING, 110);
    }

    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingConvertors.put("id", new NonUniqIdConverter("a:"));
    }
}
