package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.repository.OrienteerUserRepository;

import java.util.UUID;

/**
 * Facebook implementation for {@link org.orienteer.users.service.IOAuth2UserCreator}
 */
public class FacebookUserCreator extends AbstractUserCreator {

    private static final String FIELD_FIRST_NAME = "first_name";
    private static final String FIELD_LAST_NAME  = "last_name";
    private static final String FIELD_PICTURE    = "picture";
    private static final String FIELD_SHORT_NAME = "short_name";
    private static final String FIELD_ID         = "id";
    private static final String FIELD_EMAIL      = "email";

    @Override
    protected OrienteerUser getUserFromNode(ODatabaseDocument db, JsonNode node) {
        String email = node.get(FIELD_EMAIL) != null ? node.get(FIELD_EMAIL).textValue() : null;

        OrienteerUser user = OrienteerUserRepository.getUserByEmail(db, email).orElse(null);
        if (user != null) {
            return user;
        }

        String shortName = node.get(FIELD_SHORT_NAME).textValue();
        String id = node.get(FIELD_ID).textValue();
        return OrienteerUserRepository.getUserByName(db, createUsername(shortName, id)).orElse(null);
    }

    @Override
    protected OrienteerUser createUserFromNode(ODatabaseDocument db, JsonNode node) {
        OrienteerUser user = new OrienteerUser();
        user.setFirstName(node.get(FIELD_FIRST_NAME).textValue())
                .setLastName(node.get(FIELD_LAST_NAME).textValue())
                .setEmail(node.get(FIELD_EMAIL) != null ? node.get(FIELD_EMAIL).textValue() : null);
        user.setName(createUsername(node.get(FIELD_SHORT_NAME).textValue(), node.get(FIELD_ID).textValue()));
        user.setPassword(UUID.randomUUID().toString());
        user.setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
        user.save();

        return user;
    }

    private String createUsername(String shortName, String id) {
        return shortName + "_" + id;
    }
}
