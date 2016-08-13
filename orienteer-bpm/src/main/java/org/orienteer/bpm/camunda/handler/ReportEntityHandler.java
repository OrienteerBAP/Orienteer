package org.orienteer.bpm.camunda.handler;

import org.camunda.bpm.engine.impl.persistence.entity.DurationReportResultEntity;
import org.orienteer.bpm.camunda.handler.subentity.DurationReportResultEntityHandler;
import org.orienteer.bpm.camunda.handler.subentity.ReportResultEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * @author Kirill Mukhov
 */
public class ReportEntityHandler extends DurationReportResultEntityHandler<DurationReportResultEntity> {

    public static final String OCLASS_NAME = "BPMReport";

    public ReportEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        helper.oClass(OCLASS_NAME, ReportResultEntityHandler.OCLASS_NAME)
                .oProperty();
    }
}
