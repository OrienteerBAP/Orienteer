package org.orienteer.users.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.users.model.OrienteerUser;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface IOAuth2UserCreator extends Serializable, BiFunction<ODatabaseDocument, JsonNode, OrienteerUser> {
}
