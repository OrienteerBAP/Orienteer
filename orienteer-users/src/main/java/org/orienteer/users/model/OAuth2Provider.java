package org.orienteer.users.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.orienteer.users.service.IOAuth2UserCreator;
import org.orienteer.users.service.impl.FacebookUserCreator;
import org.orienteer.users.service.impl.GitHubUserCreator;
import org.orienteer.users.service.impl.GoogleUserCreator;

import java.util.function.Supplier;

/**
 * Default implementation of {@link IOAuth2Provider}
 * Contains base social networks.
 */
public enum OAuth2Provider implements IOAuth2Provider {

    GITHUB(
            "oauth2.provider.github",
            new PackageResourceReference(OAuth2Provider.class, "social/github.png"),
            "https://api.github.com/user",
            null,
            GitHubApi::instance,
            new GitHubUserCreator()
    ),

    FACEBOOK(
            "oauth2.provider.facebook",
            new PackageResourceReference(OAuth2Provider.class, "social/facebook.png"),
            "https://graph.facebook.com/v3.2/me?fields=first_name,last_name,email,picture,id,short_name",
            null,
            FacebookApi::instance,
            new FacebookUserCreator()
    ),

    GOOGLE(
            "oauth2.provider.google",
            new PackageResourceReference(OAuth2Provider.class, "social/google.png"),
            "https://www.googleapis.com/oauth2/v3/userinfo",
            "profile email",
            FacebookApi::instance,
            new GoogleUserCreator()
    );


    private String label;
    private ResourceReference iconResourceReference;
    private String protectedResource;
    private String scope;
    private Supplier<DefaultApi20> supplier;
    private IOAuth2UserCreator userCreator;


    OAuth2Provider(String label,
                   ResourceReference iconResourceReference,
                   String protectedResource,
                   String scope,
                   Supplier<DefaultApi20> supplier,
                   IOAuth2UserCreator userCreator) {
        this.label = label;
        this.iconResourceReference = iconResourceReference;
        this.protectedResource = protectedResource;
        this.scope = scope;
        this.supplier = supplier;
        this.userCreator = userCreator;
    }


    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public ResourceReference getIconResourceReference() {
        return iconResourceReference;
    }

    @Override
    public String getProtectedResource() {
        return protectedResource;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public DefaultApi20 getInstance() {
        return supplier.get();
    }

    @Override
    public OrienteerUser createUser(ODatabaseDocument db, JsonNode node) {
        return userCreator.apply(db, node);
    }
}