package org.orienteer.bpm.camunda.handler;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.camunda.bpm.engine.authorization.AuthorizationQuery;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.orienteer.bpm.camunda.OPersistenceSession;
import org.orienteer.core.util.OSchemaHelper;

import java.util.List;

/**
 * Created by KMukhov on 12.08.2016.
 */
public class AuthorizationEntityHandler extends AbstractEntityHandler<AuthorizationEntity> {

    public static final String OCLASS_NAME = "BPMAuthorization";

    public AuthorizationEntityHandler() {
        super(OCLASS_NAME);
    }

    @Override
    public void applySchema(OSchemaHelper helper) {
        super.applySchema(helper);

        helper.oProperty("authorizationType", OType.INTEGER, 10)
                .oProperty("groupId", OType.STRING, 20)
                .oProperty("userId", OType.STRING, 30)
                .oProperty("resourceType", OType.INTEGER, 40)
                .oProperty("resourceId", OType.STRING, 50)
                .oProperty("permission", OType.INTEGER, 60);
    }

    @Statement
    public List<AuthorizationEntity> selectAuthorizationByQueryCriteria(OPersistenceSession session, AuthorizationQuery query) {
        return query(session, query);
    }
}
