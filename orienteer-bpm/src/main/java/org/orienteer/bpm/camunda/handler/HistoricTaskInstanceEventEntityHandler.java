package org.orienteer.bpm.camunda.handler;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;


/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricTaskInstanceEventEntityHandler extends AbstractEntityHandler<HistoricTaskInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricTaskInstanceEvent";

    private static final Function<ODocument, String> GET_ID_FUNCTION = new GetODocumentFieldValueFunction<String>("id");

    public HistoricTaskInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("processDefinitionKey", OType.STRING, 10)
                .oProperty("processDefinitionId", OType.STRING, 20)
                .oProperty("processInstanceId", OType.STRING, 30)
                .oProperty("processExecutionId", OType.STRING, 40)
                .oProperty("caseDefinitionKey", OType.STRING, 50)
                .oProperty("caseDefinitionId", OType.STRING, 60)
                .oProperty("caseInstanceId", OType.STRING, 70)
                .oProperty("caseExecutionId", OType.STRING, 80)
                .oProperty("activityInstanceId", OType.STRING, 90)
                .oProperty("name", OType.STRING, 100)
                .oProperty("parentTaskId", OType.STRING, 110)
                .oProperty("description", OType.STRING, 120)
                .oProperty("owner", OType.STRING, 130)
                .oProperty("assignee", OType.STRING, 140)
                .oProperty("startTime", OType.DATETIME, 150)
                .oProperty("endTime", OType.DATETIME, 160)
                .oProperty("durationInMillis", OType.LONG, 170)
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
}
