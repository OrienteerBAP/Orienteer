package org.orienteer.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Contains settings for sending E-mail
 */
public class OMailSettings extends ODocumentWrapper {
    public static final String CLASS_NAME = "OMailSettings";
    public static final String EMAIL      = "email";
    public static final String PASSWORD   = "password";
    public static final String SMTP_HOST  = "smtpHost";
    public static final String SMTP_PORT  = "smtpPort";
    public static final String TLS_SSL    = "tlsSsl";

    public OMailSettings() {
        super(CLASS_NAME);
    }

    public OMailSettings(ODocument iDocument) {
        super(iDocument);
    }

    public OMailSettings setEmail(String email) {
        document.field(EMAIL, email);
        return this;
    }

    public String getEmail() {
        return document.field(EMAIL);
    }

    public OMailSettings setPassword(String password) {
        document.field(PASSWORD, password);
        return this;
    }

    /**
     * @return {@link String} password hash
     */
    public String getPassword() {
        return document.field(PASSWORD);
    }

    public OMailSettings setSmtpHost(String host) {
        document.field(SMTP_HOST, host);
        return this;
    }

    public String getSmtpHost() {
        return document.field(SMTP_HOST);
    }

    public OMailSettings setSmtpPort(int port) {
        document.field(SMTP_PORT, port);
        return this;
    }

    public int getSmtpPort() {
        return document.field(SMTP_PORT);
    }

    public OMailSettings setTlsSsl(boolean enable) {
        document.field(TLS_SSL, enable);
        return this;
    }

    public boolean isTlsSsl() {
        return document.field(TLS_SSL);
    }

    /**
     * Save document from admin user
     * @return {@link OMailSettings} this instance
     */
    public OMailSettings sudoSave() {
        DBClosure.sudoSave(this);
        return this;
    }
}
