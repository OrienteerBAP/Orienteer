package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.util.OUsersDbUtils;

import java.util.UUID;

public class GoogleUserCreator extends AbstractUserCreator {

    private static final String FIELD_NAME        = "name";
    private static final String FIELD_GIVEN_NAME  = "given_name";
    private static final String FIELD_FAMILY_NAME = "family_name";
    private static final String FIELD_PICTURE     = "picture";
    private static final String FIELD_EMAIL       = "email";

    @Override
    protected OrienteerUser getUserFromNode(ODatabaseDocument db, JsonNode node) {
        String email = node.get(FIELD_EMAIL).textValue();
        return OUsersDbUtils.getUserByEmail(db, email).orElse(null);
    }

    @Override
    protected OrienteerUser createUserFromNode(ODatabaseDocument db, JsonNode node) {
        String email = node.get(FIELD_EMAIL).textValue();

        OrienteerUser user = new OrienteerUser();
        user.setFirstName(node.get(FIELD_GIVEN_NAME).textValue())
                .setLastName(node.get(FIELD_FAMILY_NAME).textValue())
                .setEmail(email);
        user.setName(email);
        user.setPassword(UUID.randomUUID().toString());
        user.setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
        user.save();

        return user;
    }
}
