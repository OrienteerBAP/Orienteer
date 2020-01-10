package org.orienteer.users.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.orienteer.users.service.IOAuth2UserManager;
import org.orienteer.users.service.impl.FacebookUserManager;
import org.orienteer.users.service.impl.GitHubUserManager;
import org.orienteer.users.service.impl.GoogleUserManager;

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
            new GitHubUserManager()
    ),

    FACEBOOK(
            "oauth2.provider.facebook",
            new PackageResourceReference(OAuth2Provider.class, "social/facebook.png"),
            "https://graph.facebook.com/v5.0/me?fields=first_name,last_name,email,picture,id,short_name",
            null,
            FacebookApi::instance,
            new FacebookUserManager()
    ),

    GOOGLE(
            "oauth2.provider.google",
            new PackageResourceReference(OAuth2Provider.class, "social/google.png"),
            "https://www.googleapis.com/oauth2/v3/userinfo",
            "profile email",
            GoogleApi20::instance,
            new GoogleUserManager()
    );


    private String label;
    private ResourceReference iconResourceReference;
    private String protectedResource;
    private String scope;
    private Supplier<DefaultApi20> supplier;
    private IOAuth2UserManager userManager;


    OAuth2Provider(String label,
                   ResourceReference iconResourceReference,
                   String protectedResource,
                   String scope,
                   Supplier<DefaultApi20> supplier,
                   IOAuth2UserManager userManager) {
        this.label = label;
        this.iconResourceReference = iconResourceReference;
        this.protectedResource = protectedResource;
        this.scope = scope;
        this.supplier = supplier;
        this.userManager = userManager;
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
        return userManager.createUser(db, node);
    }

    @Override
    public OrienteerUser getUser(ODatabaseDocument db, JsonNode node) {
        return userManager.getUser(db, node);
    }
}
