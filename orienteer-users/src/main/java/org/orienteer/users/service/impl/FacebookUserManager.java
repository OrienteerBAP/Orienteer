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
 * Facebook implementation for {@link org.orienteer.users.service.IOAuth2UserManager}
 */
public class FacebookUserManager implements IOAuth2UserManager {

    private static final String FIELD_FIRST_NAME = "first_name";
    private static final String FIELD_LAST_NAME  = "last_name";
    private static final String FIELD_PICTURE    = "picture";
    private static final String FIELD_SHORT_NAME = "short_name";
    private static final String FIELD_ID         = "id";
    private static final String FIELD_EMAIL      = "email";

    @Override
    public OrienteerUser getUser(ODatabaseDocument db, JsonNode node) {
        String id = getFacebookId(node);
        OrienteerUser user = getUserById(db, id);

        if (user == null) {
            String email = node.get(FIELD_EMAIL) != null ? node.get(FIELD_EMAIL).textValue() : null;

            user = OrienteerUserRepository.getUserByEmail(db, email).orElse(null);
            if (user == null) {
                String shortName = node.get(FIELD_SHORT_NAME).textValue();
                user = OrienteerUserRepository.getUserByName(db, createUsername(shortName, id)).orElse(null);
            }
        }

        if (user != null) {
            OUsersCommonUtils.createOUserSocialNetworkIfNotExists(db, OAuth2Provider.FACEBOOK, id, user);
        }

        return user;
    }

    @Override
    public OrienteerUser createUser(ODatabaseDocument db, JsonNode node) {
        OrienteerUser user = new OrienteerUser();
        user.setFirstName(node.get(FIELD_FIRST_NAME).textValue())
                .setLastName(node.get(FIELD_LAST_NAME).textValue())
                .setEmail(node.get(FIELD_EMAIL) != null ? node.get(FIELD_EMAIL).textValue() : null);
        user.setName(createUsername(node.get(FIELD_SHORT_NAME).textValue(), node.get(FIELD_ID).textValue()));
        user.setPassword(UUID.randomUUID().toString());
        user.setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
        user.save();

        OUsersCommonUtils.createOUserSocialNetworkIfNotExists(db, OAuth2Provider.FACEBOOK, getFacebookId(node), user);

        return user;
    }

    private String createUsername(String shortName, String id) {
        return shortName + "_" + id;
    }

    private OrienteerUser getUserById(ODatabaseDocument db, String id) {
        if (id != null) {
            return OUserSocialNetworkRepository.getSocialNetworkByUserId(db, OAuth2Provider.FACEBOOK, id)
                    .map(OUserSocialNetwork::getUser)
                    .orElse(null);
        }
        return null;
    }

    private String getFacebookId(JsonNode node) {
        return node.get(FIELD_ID) != null ? node.get(FIELD_ID).textValue() : null;
    }
}
