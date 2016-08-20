package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.history.event.HistoricDetailEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * {@link IEntityHandler} for {@link HistoricDetailEventEntity}
 */
public class HistoricDetailEventEntityHandler extends HistoricEventHandler<HistoricDetailEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricDetailEvent";

    public HistoricDetailEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("timestamp", OType.DATETIME, 90)
                .oProperty("activityInstanceId", OType.STRING, 100)
                .oProperty("taskId", OType.STRING, 110)
                .oProperty("tenantId", OType.STRING, 130);
    }

    @Statement
    public List<HistoricDetailEventEntity> selectHistoricDetailsByProcessInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where processInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDetailEventEntity> selectHistoricDetailsByCaseInstanceId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where caseInstanceId = ?", parameter.getParameter());
    }

    @Statement
    public List<HistoricDetailEventEntity> selectHistoricDetailsByTaskId(OPersistenceSession session, ListQueryParameterObject parameter) {
        return queryList(session, "select from " + getSchemaClass() + " where taskId = ?", parameter.getParameter());
    }
}
