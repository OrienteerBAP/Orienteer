package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.UserOperationLogQuery;
import org.camunda.bpm.engine.impl.history.event.UserOperationLogEntryEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * {@link IEntityHandler} for {@link UserOperationLogEntryEventEntity}
 */
public class UserOperationLogEntryEventEntityHandler extends HistoricEventHandler<UserOperationLogEntryEventEntity> {

    public static final String OCLASS_NAME = "BPMUserOperationLogEntryEvent";

    public UserOperationLogEntryEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("deploymentId", OType.STRING, 10)
                .oProperty("taskId", OType.STRING, 20)
                .oProperty("jobId", OType.STRING, 30)
                .oProperty("jobDefinition", OType.STRING, 40)
                .oProperty("batchId", OType.STRING, 50)
                .oProperty("userId", OType.STRING, 60)
                .oProperty("timestamp", OType.DATETIME, 70)
                .oProperty("operationId", OType.STRING, 80)
                .oProperty("operationType", OType.STRING, 90)
                .oProperty("entityType", OType.STRING, 100)
                .oProperty("property", OType.STRING, 110)
                .oProperty("orgValue", OType.STRING, 120)
                .oProperty("newValue", OType.STRING, 130)
                .oProperty("tenantId", OType.STRING, 140);
    }

    @Statement
    public List<UserOperationLogEntryEventEntity> selectUserOperationLogEntriesByQueryCriteria(OPersistenceSession session,
                                                                                               UserOperationLogQuery query) {
        return query(session, query);
    }
}