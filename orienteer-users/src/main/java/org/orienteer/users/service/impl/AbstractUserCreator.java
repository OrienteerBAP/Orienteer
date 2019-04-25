package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.service.IOAuth2UserCreator;

/**
 * Abstract implementation of {@link IOAuth2UserCreator}
 */
public abstract class AbstractUserCreator implements IOAuth2UserCreator {

    @Override
    public final OrienteerUser apply(ODatabaseDocument db, JsonNode jsonNode) {
        OrienteerUser user = getUserFromNode(db, jsonNode);
        if (user != null) {
            return user;
        }
        user = createUserFromNode(db, jsonNode);

        return user;
    }

    /**
     * Try to retrieve user from database by using data from JSON node
     * @param db database
     * @param node JSON node
     * @return user if it was found in database
     */
    protected abstract OrienteerUser getUserFromNode(ODatabaseDocument db, JsonNode node);

    /**
     * Create new user from JSON node
     * @param db database
     * @param node JSON node
     * @return new user
     */
    protected abstract OrienteerUser createUserFromNode(ODatabaseDocument db, JsonNode node);
}
