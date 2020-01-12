package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.orienteer.users.model.OAuth2Provider;
import org.orienteer.users.model.OUserSocialNetwork;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.repository.OUserSocialNetworkRepository;
import org.orienteer.users.repository.OrienteerUserRepository;
import org.orienteer.users.service.IOAuth2UserManager;
import org.orienteer.users.util.OUsersCommonUtils;

import java.util.UUID;

/**
 * Google implementation for {@link org.orienteer.users.service.IOAuth2UserManager}
 */
public class GoogleUserManager implements IOAuth2UserManager {

    private static final String FIELD_NAME        = "name";
    private static final String FIELD_GIVEN_NAME  = "given_name";
    private static final String FIELD_FAMILY_NAME = "family_name";
    private static final String FIELD_PICTURE     = "picture";
    private static final String FIELD_EMAIL       = "email";
    private static final String FIELD_SUB         = "sub";

    @Override
    public OrienteerUser getUser(ODatabaseDocument db, JsonNode node) {
        String id = getGoogleId(node);
        OrienteerUser user = getUserById(db, id);

        if (user == null) {
            String email = node.get(FIELD_EMAIL).textValue();
            user = OrienteerUserRepository.getUserByEmail(db, email).orElse(null);
        }

        if (user != null) {
            OUsersCommonUtils.createOUserSocialNetworkIfNotExists(db, OAuth2Provider.GOOGLE, id, user);
        }

        return user;
    }

    @Override
    public OrienteerUser createUser(ODatabaseDocument db, JsonNode node) {
        String email = node.get(FIELD_EMAIL).textValue();

        OrienteerUser user = new OrienteerUser();
        user.setFirstName(node.get(FIELD_GIVEN_NAME).textValue())
                .setLastName(node.get(FIELD_FAMILY_NAME).textValue())
                .setEmail(email);
        user.setName(email);
        user.setPassword(UUID.randomUUID().toString());
        user.setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
        user.save();

        OUsersCommonUtils.createOUserSocialNetworkIfNotExists(db, OAuth2Provider.GOOGLE, getGoogleId(node), user);

        return user;
    }

    private OrienteerUser getUserById(ODatabaseDocument db, String id) {
        if (id != null) {
            return OUserSocialNetworkRepository.getSocialNetworkByUserId(db, OAuth2Provider.GOOGLE, id)
                    .map(OUserSocialNetwork::getUser)
                    .orElse(null);
        }
        return null;
    }

    private String getGoogleId(JsonNode node) {
        return node.get(FIELD_SUB) != null ? node.get(FIELD_SUB).textValue() : null;
    }
}
