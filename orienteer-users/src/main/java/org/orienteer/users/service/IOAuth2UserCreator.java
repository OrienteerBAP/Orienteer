package org.orienteer.users.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.users.model.OrienteerUser;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Interface for create user in {@link org.orienteer.users.model.OAuth2Provider}
 */
public interface IOAuth2UserCreator extends Serializable, BiFunction<ODatabaseDocument, JsonNode, OrienteerUser> {
}
