package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.impl.persistence.entity.MembershipEntity;
import org.orienteer.core.util.OSchemaHelper;

/**
 * @author Kirill Mukhov
 */
public class MembershipEntityHandler extends AbstractEntityHandler<MembershipEntity> {

    public static final String OCLASS_NAME = "BPMMembership";

    public MembershipEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("userId", OType.STRING, 10)
                .oProperty("groupId", OType.STRING, 20);
    }
}
