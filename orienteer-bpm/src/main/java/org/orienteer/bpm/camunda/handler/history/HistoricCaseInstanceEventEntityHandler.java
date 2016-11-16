package org.orienteer.bpm.camunda.handler.history;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricCaseInstanceEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.GetODocumentFieldValueFunction;

import java.util.List;

/**
 * {@link IEntityHandler} for {@link HistoricCaseInstanceEventEntity}
 */
public class HistoricCaseInstanceEventEntityHandler extends HistoricScopeInstanceEventHandler<HistoricCaseInstanceEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricCaseInstanceEvent";

    private static final Function<ODocument, String> GET_ID_FUNCTION = new GetODocumentFieldValueFunction<String>("id");

    public HistoricCaseInstanceEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricScopeInstanceEventHandler.OCLASS_NAME)
                .oProperty("businessKey", OType.STRING, 20)
                .oProperty("state", OType.INTEGER, 70)
                .oProperty("createUserId", OType.STRING, 80)
                .oProperty("superCaseInstanceId", OType.STRING, 90)
                .oProperty("superProcessInstanceId", OType.STRING, 100);
    }

    @Statement
    public List<String> selectHistoricCaseInstanceIdsByCaseDefinitionId(OPersistenceSession session, ListQueryParameterObject parameter) {
        ODatabaseDocument db = session.getDatabase();
        List<ODocument> resultSet = db.query(new OSQLSynchQuery<>("select id from "+getSchemaClass()+" where caseDefinitionId = ?"), parameter.getParameter());
        return Lists.transform(resultSet, GET_ID_FUNCTION);
    }
}
