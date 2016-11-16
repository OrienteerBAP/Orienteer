package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.UserOperationLogQuery;
import org.camunda.bpm.engine.impl.history.event.UserOperationLogEntryEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.*;
import org.orienteer.core.OClassDomain;
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
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("deploymentId", OType.STRING, 10)
                .oProperty("task", OType.LINK, 20)
                .oProperty("job", OType.LINK, 30)
                .oProperty("jobDefinition", OType.LINK, 40)
                .oProperty("batch", OType.LINK, 50)
                .oProperty("user", OType.LINK, 60)
                .oProperty("timestamp", OType.DATETIME, 70)
                .oProperty("operationId", OType.STRING, 80)
                .oProperty("operationType", OType.STRING, 90)
                .oProperty("entityType", OType.STRING, 100)
                .oProperty("property", OType.STRING, 110)
                .oProperty("orgValue", OType.STRING, 120)
                .oProperty("newValue", OType.STRING, 130);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);

        helper.setupRelationship(OCLASS_NAME, "task", TaskEntityHandler.OCLASS_NAME, "userOperationLogEntryEvents");
        helper.setupRelationship(OCLASS_NAME, "job", JobEntityHandler.OCLASS_NAME, "userOperationLogEntryEvents");
        helper.setupRelationship(OCLASS_NAME, "jobDefinition", JobDefinitionEntityHandler.OCLASS_NAME, "userOperationLogEntryEvents");
        helper.setupRelationship(OCLASS_NAME, "batch", BatchEntityHandler.OCLASS_NAME, "userOperationLogEntryEvents");
        helper.setupRelationship(OCLASS_NAME, "user", UserEntityHandler.OCLASS_NAME, "userOperationLogEntryEvents");
    }

    @Statement
    public List<UserOperationLogEntryEventEntity> selectUserOperationLogEntriesByQueryCriteria(OPersistenceSession session,
                                                                                               UserOperationLogQuery query) {
        return query(session, query);
    }
}