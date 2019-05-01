package org.orienteer.users.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.users.model.OrienteerUser;

import java.io.Serializable;

public interface IOAuth2UserManager extends Serializable {

    /**
     * Try to get user from database by given node
     * @param db database
     * @param node JSON node
     * @return user which exists in database or null
     */
    OrienteerUser getUser(ODatabaseDocument db, JsonNode node);

    /**
     * Try to create user in database by given node.
     * If user which node contains already exists in database so will return user
     * @param db database
     * @param node JSON node which contains user
     * @return new user or user which already exists in database. If can't retrieve information about user from JSON node,
     * so return null
     */
    OrienteerUser createUser(ODatabaseDocument db, JsonNode node);
}
