package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.batch.history.HistoricBatchQuery;
import org.camunda.bpm.engine.impl.batch.history.HistoricBatchEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.bpm.camunda.handler.subentity.HistoricEventHandler;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by KMukhov on 07.08.16.
 */
public class HistoricBatchEntityHandler extends HistoricEventHandler<HistoricBatchEntity> {

    public static final String OCLASS_NAME = "BPMHistoricBatch";

    public HistoricBatchEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, HistoricEventHandler.OCLASS_NAME)
                .oProperty("type", OType.STRING, 10)
                .oProperty("totalJobs", OType.INTEGER, 20)
                .oProperty("batchJobsPerSeed", OType.INTEGER, 30)
                .oProperty("invocationsPerBatchJob", OType.INTEGER, 40)
                .oProperty("seedJobDefinitionId", OType.STRING, 50)
                .oProperty("monitorJobDefinitionId", OType.STRING, 60)
                .oProperty("batchJobDefinitionId", OType.STRING, 70)
                .oProperty("tenantId", OType.STRING, 80)
                .oProperty("startTime", OType.DATETIME, 90)
                .oProperty("endTime", OType.DATETIME, 100);
    }

    @Statement
    public List<HistoricBatchEntity> selectHistoricBatchesByQueryCriteria(OPersistenceSession session, HistoricBatchQuery query) {
        return query(session, query);
    }
}
