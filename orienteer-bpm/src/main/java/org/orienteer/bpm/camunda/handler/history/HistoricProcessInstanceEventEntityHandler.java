package org.orienteer.bpm.camunda.handler.history;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricProcessInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.*;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link HistoricProcessInstanceEventEntity}
 */
public class HistoricProcessInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricProcessInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricProcessInstanceEvent";

    public HistoricProcessInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    private static final Function<ODocument, String> GET_ID_FUNCTION = new GetODocumentFieldValueFunction<String>("id");

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("parentActivityInstanceId", OType.STRING, 10)
                .oProperty("activityId", OType.STRING, 60)
                .oProperty("task", OType.LINK, 70)
                .oProperty("calledProcessInstanceId", OType.STRING, 80)
                .oProperty("calledCaseInstanceId", OType.STRING, 90)
                .oProperty("activityName", OType.STRING, 100)
                .oProperty("activityType", OType.STRING, 110)
                .oProperty("taskAssignee", OType.STRING, 120)
                .oProperty("activityInstanceState", OType.INTEGER, 160);

    }
    
    @Override
    protected void initMapping(OPersistenceSession session) {
    	super.initMapping(session);
    	mappingConvertors.put("id", new NonUniqIdConverter("pi:"));
    }

    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);
        helper.setupRelationship(OCLASS_NAME, "task", TaskEntityHandler.OCLASS_NAME, "historicProcessInstances");
    }

    @Statement
    public List<String> selectHistoricProcessInstanceIdsByProcessDefinitionId(OPersistenceSession session, ListQueryParameterObject parameter) {
        ODatabaseDocument db = session.getDatabase();
        List<ODocument> resultSet = db.query(new OSQLSynchQuery<>("select id from "+getSchemaClass()+" where processDefinition.id = ?"), parameter.getParameter());
        return Lists.transform(resultSet, GET_ID_FUNCTION);
    }

    @Statement
    public void deleteHistoricProcessInstance(OPersistenceSession session, String processInstanceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("processInstanceId", processInstanceId);

        delete(session, params);
    }
}
