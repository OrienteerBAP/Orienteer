package org.orienteer.bpm.camunda.handler.history;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.NonUniqIdConverter;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

import java.util.List;
import java.util.Map;


/**
 * {@link IEntityHandler} for {@link HistoricTaskInstanceEventEntity}
 */
public class HistoricTaskInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricTaskInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricTaskInstanceEvent";

    private static final Function<ODocument, String> GET_ID_FUNCTION = new GetODocumentFieldValueFunction<String>("id");

    public HistoricTaskInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("processExecutionId", OType.STRING, 40)
                .oProperty("activityInstanceId", OType.STRING, 90)
                .oProperty("name", OType.STRING, 100)
                .oProperty("parentTaskId", OType.STRING, 110)
                .oProperty("description", OType.STRING, 120)
                .oProperty("owner", OType.STRING, 130)
                .oProperty("assignee", OType.STRING, 140)
                .oProperty("deleteReason", OType.STRING, 180)
                .oProperty("taskDefinitionKey", OType.STRING, 190)
                .oProperty("priority", OType.INTEGER, 200)
                .oProperty("dueDate", OType.DATETIME, 210)
                .oProperty("followUpDate", OType.DATETIME, 220)
                .oProperty("tenantId", OType.STRING, 230);
    }

    @Override
    protected void initMapping(OPersistenceSession session) {
        super.initMapping(session);
        mappingConvertors.put("id", new NonUniqIdConverter("ti:"));
    }

    // TODO
//    @Statement
//    public String selectHistoricTaskInstanceIdsByParameters(OPersistenceSession session, ListQueryParameterObject parameters) {
//        Map<String, String> params = (Map<String, String>) parameters.getParameter();
//    }
}
