package org.orienteer.users.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * OAuth2 context
 * Uses for login user
 */
public class OAuth2ServiceContext extends ODocumentWrapper {

    public static final String CLASS_NAME = "OAuth2ServiceContext";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Contains current state received in OAuth2 communication
     */
    public static final String PROP_STATE             = "state";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#LINK}
     * Links to {@link OAuth2Service} which was used for create this context
     */
    public static final String PROP_SERVICE           = "service";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#BOOLEAN}
     * If true - so this context was already used and it shouldn't use again
     */
    public static final String PROP_USED              = "used";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Contains authorization url
     */
    public static final String PROP_AUTHORIZATION_URL = "authorizationUrl";

    public OAuth2ServiceContext() {
        this(CLASS_NAME);
    }

    public OAuth2ServiceContext(ORID iRID) {
        super(iRID);
    }

    public OAuth2ServiceContext(String iClassName) {
        super(iClassName);
    }

    public OAuth2ServiceContext(ODocument iDocument) {
        super(iDocument);
    }

    public OAuth2ServiceContext setState(String state) {
        document.field(PROP_STATE, state);
        return this;
    }

    public String getState() {
        return document.field(PROP_STATE);
    }

    public OAuth2Service getService() {
        ODocument doc = getServiceAsDocument();
        return doc != null ? new OAuth2Service(doc) : null;
    }

    public ODocument getServiceAsDocument() {
        OIdentifiable identifiable = document.field(PROP_SERVICE);
        return identifiable != null ? identifiable.getRecord() : null;
    }

    public boolean isUsed() {
        return document.field(PROP_USED);
    }

    public OAuth2ServiceContext setUsed(boolean used) {
        document.field(PROP_USED, used);
        return this;
    }

    public OAuth2ServiceContext setService(OAuth2Service service) {
        return setServiceAsDocument(service != null ? service.getDocument() : null);
    }

    public OAuth2ServiceContext setServiceAsDocument(ODocument service) {
        document.field(PROP_SERVICE, service);
        return this;
    }

    public String getAuthorizationUrl() {
        return document.field(PROP_AUTHORIZATION_URL);
    }

    public OAuth2ServiceContext setAuthorizationUrl(String url) {
        document.field(PROP_AUTHORIZATION_URL, url);
        return this;
    }
}
