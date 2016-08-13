package org.orienteer.bpm.camunda.handler.subentity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.history.DurationReportResult;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.orienteer.core.util.OSchemaHelper;

/**
 * @author Kirill Mukhov
 */
public class DurationReportResultEntityHandler<T extends DurationReportResult & DbEntity> extends ReportResultEntityHandler<T> {

    public static final String OCLASS_NAME = "BPMDurationReportResult";

    public DurationReportResultEntityHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, ReportResultEntityHandler.OCLASS_NAME)
                .oProperty("minimum", OType.LONG, 10)
                .oProperty("maximum", OType.LONG, 20)
                .oProperty("average", OType.LONG, 30);

    }
}
