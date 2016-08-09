package org.orienteer.bpm.camunda.handler;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricCaseInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.historic.HistoricScopeInstanceEventHandler;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

import java.util.List;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricCaseInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricCaseInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricCaseInstanceEvent";

    private static final Function<ODocument, String> GET_ID_FUNCTION = new GetODocumentFieldValueFunction<String>("id");

    public HistoricCaseInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("businessKey", OType.STRING, 20)
                .oProperty("state", OType.INTEGER, 70)
                .oProperty("createUserId", OType.STRING, 80)
                .oProperty("superCaseInstanceId", OType.STRING, 90)
                .oProperty("superProcessInstanceId", OType.STRING, 100)
                .oProperty("tenantId", OType.STRING, 110);
    }

    public List<String> selectHistoricCaseInstanceIdsByCaseDefinitionId(OPersistenceSession session, ListQueryParameterObject parameter) {
        ODatabaseDocument db = session.getDatabase();
        List<ODocument> resultSet = db.query(new OSQLSynchQuery<>("select id from "+getSchemaClass()+" where caseDefinitionId = ?"), parameter.getParameter());
        return Lists.transform(resultSet, GET_ID_FUNCTION);
    }
}
