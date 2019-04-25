package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.repository.OrienteerUserRepository;

import java.util.UUID;

/**
 * GitHub implementation for {@link org.orienteer.users.service.IOAuth2UserCreator}
 */
public class GitHubUserCreator extends AbstractUserCreator {

    private static final String FIELD_LOGIN    = "login";
    private static final String FIELD_NAME     = "name";
    private static final String FIELD_EMAIL    = "email";
    private static final String FIELD_BIO      = "bio";
    private static final String FIELD_BLOG     = "blog";
    private static final String FIELD_LOCATION = "location";

    @Override
    protected OrienteerUser getUserFromNode(ODatabaseDocument db, JsonNode node) {
        String login = node.get(FIELD_LOGIN).textValue();
        String email = node.get(FIELD_EMAIL).textValue();

        db.getMetadata().getSecurity().getUser(login);

        OrienteerUser user = OrienteerUserRepository.getUserByName(db, login).orElse(null);
        if (user == null) {
            user = OrienteerUserRepository.getUserByEmail(db, email).orElse(null);
        }
        return user;
    }

    @Override
    protected OrienteerUser createUserFromNode(ODatabaseDocument db, JsonNode node) {
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
        return user;
    }
}
