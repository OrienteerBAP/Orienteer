package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
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
 * GitHub implementation for {@link org.orienteer.users.service.IOAuth2UserManager}
 */
public class GitHubUserManager implements IOAuth2UserManager {

    private static final String FIELD_LOGIN    = "login";
    private static final String FIELD_NAME     = "name";
    private static final String FIELD_EMAIL    = "email";
    private static final String FIELD_BIO      = "bio";
    private static final String FIELD_BLOG     = "blog";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_ID       = "id";

    @Override
    public OrienteerUser getUser(ODatabaseDocument db, JsonNode node) {
        OrienteerUser user = getUserByGitHubId(db, node);

        if (user == null) {
            user = getUserByEmailOrName(db, node);
        }
        if (user != null) {
            OUsersCommonUtils.createOUserSocialNetworkIfNotExists(db, OAuth2Provider.GITHUB, getGitHubId(node), user);
        }
        return user;
    }

    @Override
    public OrienteerUser createUser(ODatabaseDocument db, JsonNode node) {
        String name = node.get(FIELD_NAME).textValue();
        String firstName;
        String lastName;

        if (!Strings.isNullOrEmpty(name) && name.contains(" ")) {
            String[] firstAndLastName = name.split(" ");
            firstName = firstAndLastName[0];
            lastName = firstAndLastName[1];
        } else {
            firstName = name;
            lastName = null;
        }

        String login = node.get(FIELD_LOGIN).textValue();

        OrienteerUser user = new OrienteerUser();

        user.setName(login);
        user.setPassword(UUID.randomUUID().toString());

        user.setEmail(node.get(FIELD_EMAIL).textValue())
                .setFirstName(firstName)
                .setLastName(lastName)
                .setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
        user.save();

        OUsersCommonUtils.createOUserSocialNetworkIfNotExists(db, OAuth2Provider.GITHUB, getGitHubId(node), user);

        return user;
    }

    private OrienteerUser getUserByGitHubId(ODatabaseDocument db, JsonNode node) {
        String id = getGitHubId(node);
        if (id != null) {
            return OUserSocialNetworkRepository.getSocialNetworkByUserId(db, OAuth2Provider.GITHUB, id)
                    .map(OUserSocialNetwork::getUser)
                    .orElse(null);
        }
        return null;
    }

    private OrienteerUser getUserByEmailOrName(ODatabaseDocument db, JsonNode node) {
        String login = node.get(FIELD_LOGIN).textValue();
        String email = node.get(FIELD_EMAIL).textValue();

        db.getMetadata().getSecurity().getUser(login);

        OrienteerUser user = OrienteerUserRepository.getUserByName(db, login).orElse(null);
        if (user == null) {
            user = OrienteerUserRepository.getUserByEmail(db, email).orElse(null);
        }
        return user;
    }


    private String getGitHubId(JsonNode node) {
        return node.get(FIELD_ID) != null ? Integer.toString(node.get(FIELD_ID).intValue()) : null;
    }
}
