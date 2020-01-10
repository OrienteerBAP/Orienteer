package org.orienteer.users.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

public class OUserSocialNetwork extends ODocumentWrapper {

    public static final String CLASS_NAME = "OUserSocialNetwork";

    public static final String PROP_USER_ID  = "userId";
    public static final String PROP_PROVIDER = "provider";
    public static final String PROP_USER     = "user";

    public OUserSocialNetwork() {
        this(CLASS_NAME);
    }

    public OUserSocialNetwork(String iClassName) {
        super(iClassName);
    }

    public OUserSocialNetwork(ODocument iDocument) {
        super(iDocument);
    }

    public String getUserId() {
        return document.field(PROP_USER_ID);
    }

    public OUserSocialNetwork setUserId(String userId) {
        document.field(PROP_USER_ID, userId);
        return this;
    }

    public IOAuth2Provider getProvider() {
        String alias = document.field(PROP_PROVIDER);
        return OAuth2Provider.valueOf(alias);
    }


    public OUserSocialNetwork setProvider(IOAuth2Provider provider) {
        return setProviderAsAlias(provider != null ? provider.getName() : null);
    }

    public OUserSocialNetwork setProviderAsAlias(String provider) {
        document.field(PROP_PROVIDER, provider);
        return this;
    }

    public OrienteerUser getUser() {
        ODocument user = getUserAsDocument();
        return user != null ? new OrienteerUser(user) : null;
    }

    public ODocument getUserAsDocument() {
        OIdentifiable user = document.field(PROP_USER);
        return user != null ? user.getRecord() : null;
    }

    public OUserSocialNetwork setUser(OrienteerUser user) {
        return setUserAsDocument(user != null ? user.getDocument() : null);
    }

    public OUserSocialNetwork setUserAsDocument(ODocument user) {
        document.field(PROP_USER, user);
        return this;
    }
}
