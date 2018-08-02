package org.orienteer.users.model;

import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.time.Instant;
import java.util.Date;

/**
 * DocumentWrapper for more specialized work with OrienteerUser 
 */
public class OrienteerUser extends OUser {
    private static final long serialVersionUID = 1L;

    public static final String PROP_ID                 = "id";
    public static final String PROP_RESTORE_ID         = "restoreId";
    public static final String PROP_RESTORE_ID_CREATED = "restoreIdCreated";
    public static final String PROP_EMAIL              = "email";

    public OrienteerUser(String className) {
        this(new ODocument(className));
    }

    public OrienteerUser(String iUserName, String iUserPassword) {
        super(iUserName, iUserPassword);
    }

    public OrienteerUser(ODocument iSource) {
        super(iSource);
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
}
