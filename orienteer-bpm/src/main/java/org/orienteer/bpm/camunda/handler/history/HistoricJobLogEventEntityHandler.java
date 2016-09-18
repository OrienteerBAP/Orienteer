package org.orienteer.bpm.camunda.handler.history;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricJobLogEventEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.DeploymentEntityHandler;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.bpm.camunda.handler.JobDefinitionEntityHandler;
import org.orienteer.bpm.camunda.handler.Statement;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link IEntityHandler} for {@link HistoricJobLogEventEntity}
 */
public class HistoricJobLogEventEntityHandler extends HistoricEventHandler<HistoricJobLogEventEntity> {

    public static final String OCLASS_NAME = "BPMHistoricJobLogEvent";

    public HistoricJobLogEventEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
    	super.applySchema(helper);
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME).domain(OClassDomain.SYSTEM)
                .oProperty("timestamp", OType.DATETIME, 10)
                .oProperty("jobId", OType.STRING, 20)
                .oProperty("jobDueDate", OType.DATETIME, 30)
                .oProperty("jobRetries", OType.INTEGER, 40)
                .oProperty("jobPriority", OType.LONG, 50)
                .oProperty("jobExceptionMessage", OType.STRING, 60)
                .oProperty("exceptionByteArrayId", OType.STRING, 70)
                .oProperty("state", OType.INTEGER, 80)
                .oProperty("jobDefinition", OType.LINK, 90)
                .oProperty("jobDefinitionType", OType.STRING, 100)
                .oProperty("jobDefinitionConfiguration", OType.STRING, 110)
                .oProperty("activityId", OType.STRING, 120)
                .oProperty("deployment", OType.LINK, 170);
    }

    @Override
    public void applyRelationships(OSchemaHelper helper) {
        super.applyRelationships(helper);

        helper.setupRelationship(OCLASS_NAME, "jobDefinition", JobDefinitionEntityHandler.OCLASS_NAME, "historyJobLogEvents");
        helper.setupRelationship(OCLASS_NAME, "deployment", DeploymentEntityHandler.OCLASS_NAME);
    }

    @Statement
    public void deleteHistoricJobLogByDeploymentId(OPersistenceSession session, String deploymentId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("deploymentId", deploymentId);

        delete(session, params);
    }

    @Statement
    public void deleteHistoricJobLogByProcessDefinitionId(OPersistenceSession session, String processDefinitionId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("processDefinitionId", processDefinitionId);

        delete(session, params);
    }

    @Statement
    public void deleteHistoricJobLogByProcessInstanceId(OPersistenceSession session, String processInstanceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("processInstanceId", processInstanceId);

        delete(session, params);
    }
}
