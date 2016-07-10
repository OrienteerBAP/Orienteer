package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by kir on 10.07.16.
 */
public class TenantEntityHandler extends AbstractEntityHandler<TenantEntity> {

    public TenantEntityHandler() {
        super("BPMTenant");
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("name", OType.STRING, 10);
    }

    @Statement
    public List<TenantEntity> selectTenantByQueryCriteria(OPersistenceSession session, TenantQuery query) {
        return query(session, query);
    }
}
