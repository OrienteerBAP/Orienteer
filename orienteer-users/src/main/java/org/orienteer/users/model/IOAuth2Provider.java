package org.orienteer.users.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.apache.wicket.request.resource.ResourceReference;

import java.io.Serializable;

public interface IOAuth2Provider extends Serializable {
    String getName();

    String getLabel();
    ResourceReference getIconResourceReference();

    String getProtectedResource();
    String getScope();
    DefaultApi20 getInstance();
    OrienteerUser createUser(ODatabaseDocument db, JsonNode node);
}
