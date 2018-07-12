package org.orienteer.users.model;

import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.time.Instant;
import java.util.Date;

public class OrienteerUser extends OUser {
    private static final long serialVersionUID = 1L;

    public static final String CLASS_NAME = "OrienteerUser";

    public static final String PROP_ID                = "id";
    public static final String PROP_RESTORE_ID        = "restoreId";
    public static final String PROP_RESTORE_ID_CREATED = "restoreIdCreated";

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

    @SuppressWarnings("unchecked")
    public <T extends OrienteerUser> T setRestoreId(String restoreId) {
        document.field(PROP_RESTORE_ID, restoreId);
        return (T) this;
    }

    public Instant getRestoreIdCreated() {
        Date date = document.field(PROP_RESTORE_ID_CREATED);
        return date != null ? date.toInstant() : null;
    }

    public <T extends OrienteerUser> T setRestoreIdCreated(Instant instant) {
        return setRestoreIdCreated(Date.from(instant));
    }

    @SuppressWarnings("unchecked")
    public <T extends OrienteerUser> T setRestoreIdCreated(Date date) {
        document.field(PROP_RESTORE_ID_CREATED, date);
        return (T) this;
    }
}
