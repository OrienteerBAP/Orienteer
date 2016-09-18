package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.TenantMembershipEntity;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.util.OSchemaHelper;

/**
 * {@link IEntityHandler} for {@link TenantMembershipEntity}
 * TODO: Refactor class - it's redundant
 */
public class TenantMembershipEntityHandler extends AbstractEntityHandler<TenantMembershipEntity> {

    public static final String OCLASS_NAME = "BPMTenantMembership";

    public TenantMembershipEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);
        helper.domain(OClassDomain.SYSTEM);
        helper.oProperty("tenantId", OType.STRING, 10)
                .oProperty("userId", OType.STRING, 20)
                .oProperty("groupId", OType.STRING, 30);
    }
}
