package org.orienteer.users.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.apache.wicket.request.resource.ResourceReference;

import java.io.Serializable;

/**
 * OAuth2 provider.
 * Used by {@link org.orienteer.users.service.IOAuth2Service} for login user throughout social networks
 */
public interface IOAuth2Provider extends Serializable {

    /**
     * @return unique name of provider
     */
    String getName();

    /**
     * @return key of Wicket resource for display for user
     */
    String getLabel();

    /**
     * @return resource reference which contains logo icon of this provider
     */
    ResourceReference getIconResourceReference();

    /**
     * @see <a href="https://github.com/scribejava/scribejava">ScribeJava</a>
     * @return url to protected resource which contains user data
     */
    String getProtectedResource();

    /**
     * @see <a href="https://github.com/scribejava/scribejava">ScribeJava</a>
     * @return scope of this provider
     */
    String getScope();

    /**
     * @return instance of {@link DefaultApi20}
     */
    DefaultApi20 getInstance();

    /**
     * Create user by this provider
     * @param db database
     * @param node JSON node which was received by using this provider
     * @return user from node or null. If user already exists in system, so will returns this user.
     */
    OrienteerUser createUser(ODatabaseDocument db, JsonNode node);
}
