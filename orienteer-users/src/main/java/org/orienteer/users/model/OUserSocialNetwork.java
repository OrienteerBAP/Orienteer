package org.orienteer.users.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Data class for user account to user social network account
 */
public class OUserSocialNetwork extends ODocumentWrapper {

    public static final String CLASS_NAME = "OUserSocialNetwork";

    public static final String PROP_USER_ID = "userId";
    public static final String PROP_SERVICE = "service";
    public static final String PROP_USER    = "user";

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

    public OAuth2Service getService() {
        ODocument service = getServiceAsDocument();
        return service != null ? new OAuth2Service(service) : null;
    }

    public ODocument getServiceAsDocument() {
        OIdentifiable service = document.field(PROP_SERVICE);
        return service != null ? service.getRecord() : null;
    }


    public OUserSocialNetwork setService(OAuth2Service service) {
        return setServiceAsDocument(service.getDocument());
    }

    public OUserSocialNetwork setServiceAsDocument(ODocument service) {
        document.field(PROP_SERVICE, service);
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
