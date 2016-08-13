package org.orienteer.bpm.camunda.handler.subentity;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.orienteer.bpm.camunda.handler.AbstractEntityHandler;
import org.orienteer.core.util.OSchemaHelper;

/**
 * @author Kirill Mukhov
 */
public class ReportResultEntityHandler<T extends DbEntity> extends AbstractEntityHandler<T> {

    public static final String OCLASS_NAME = "BPMReportResult";

    public ReportResultEntityHandler(String schemaClass) {
        super(schemaClass);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("period", OType.INTEGER, 10)
                .oProperty("periodUnit", OType.STRING, 20);
    }
}
