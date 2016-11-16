package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.FilterEntity;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;


/**
 * {@link IEntityHandler} for {@link FilterEntity}
 */
public class FilterEntityHandler extends AbstractEntityHandler<FilterEntity> {

    public static final String OCLASS_NAME = "BPMFilter";

    public FilterEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.domain(OClassDomain.SYSTEM);
        helper.oProperty("resourceType", OType.STRING, 10)
                .oProperty("name", OType.STRING, 20)
                .oProperty("owner", OType.STRING, 30)
                .oProperty("queryInternal", OType.BYTE, 40)
                .oProperty("propertiesInternal", OType.BYTE, 50);
    }
}
