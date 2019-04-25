package org.orienteer.users.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.service.IOAuth2UserCreator;

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

    protected abstract OrienteerUser getUserFromNode(ODatabaseDocument db, JsonNode node);
    protected abstract OrienteerUser createUserFromNode(ODatabaseDocument db, JsonNode node);
}
