package org.orienteer.users.model;

import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.util.CommonUtils;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DocumentWrapper for more specialized work with OrienteerUser 
 */
public class OrienteerUser extends OUser {
    private static final long serialVersionUID = 1L;

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Unique user id
     */
    public static final String PROP_ID                 = "id";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Restore id which uses for restore user password. Contains null if user doesn't restore password
     */
    public static final String PROP_RESTORE_ID         = "restoreId";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#DATETIME}
     * Timestamp when {@link OrienteerUser#PROP_RESTORE_ID} was created
     */
    public static final String PROP_RESTORE_ID_CREATED = "restoreIdCreated";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Unique user email
     */
    public static final String PROP_EMAIL              = "email";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * User first name
     */
    public static final String PROP_FIRST_NAME         = "firstName";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * User last name
     */
    public static final String PROP_LAST_NAME          = "lastName";

    public static final String PROP_SOCIAL_NETWORKS    = "socialNetworks";

    public OrienteerUser(String className) {
        this(new ODocument(className));
    }

    public OrienteerUser() {
        this(CLASS_NAME);
    }

    public OrienteerUser(String iUserName, String iUserPassword) {
        super(iUserName, iUserPassword);
    }

    public OrienteerUser(ODocument iSource) {
        super(iSource);
    }

    public String getFirstName() {
        return document.field(PROP_FIRST_NAME);
    }

    public OrienteerUser setFirstName(String firstName) {
        document.field(PROP_FIRST_NAME, firstName);
        return this;
    }

    public String getLastName() {
        return document.field(PROP_LAST_NAME);
    }

    public OrienteerUser setLastName(String lastName) {
        document.field(PROP_LAST_NAME, lastName);
        return this;
    }

    public String getId() {
        return document.field(PROP_ID);
    }

    public String getRestoreId() {
        return document.field(PROP_RESTORE_ID);
    }

    public OrienteerUser setRestoreId(String restoreId) {
        document.field(PROP_RESTORE_ID, restoreId);
        return this;
    }

    public Instant getRestoreIdCreated() {
        Date date = document.field(PROP_RESTORE_ID_CREATED);
        return date != null ? date.toInstant() : null;
    }

    public OrienteerUser setRestoreIdCreated(Instant instant) {
        return setRestoreIdCreatedAsDate(instant != null ? Date.from(instant) : null);
    }

    public OrienteerUser setRestoreIdCreatedAsDate(Date date) {
        document.field(PROP_RESTORE_ID_CREATED, date);
        return this;
    }

    public String getEmail() {
        return document.field(PROP_EMAIL);
    }

    public OrienteerUser setEmail(String email) {
        document.field(PROP_EMAIL, email);
        return this;
    }

    public List<OUserSocialNetwork> getSocialNetworks() {
        return getSocialNetworksAsDocuments().stream()
                .map(OUserSocialNetwork::new)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<ODocument> getSocialNetworksAsDocuments() {
        return CommonUtils.getDocuments(document.field(PROP_SOCIAL_NETWORKS));
    }

    public OrienteerUser setSocialNetworks(List<OUserSocialNetwork> networks) {
        List<ODocument> docs = networks.stream()
                .map(OUserSocialNetwork::getDocument)
                .collect(Collectors.toCollection(LinkedList::new));
        return setSocialNetworksAsDocuments(docs);
    }

    public OrienteerUser setSocialNetworksAsDocuments(List<ODocument> networks) {
        document.field(PROP_SOCIAL_NETWORKS, networks);
        return this;
    }
}
