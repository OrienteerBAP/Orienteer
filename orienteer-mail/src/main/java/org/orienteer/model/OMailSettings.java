package org.orienteer.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Contains settings for sending E-mail
 */
public class OMailSettings extends ODocumentWrapper {
    public static final String CLASS_NAME = "OMailSettings";

    public static final String OPROPERTY_EMAIL     = "email";
    public static final String OPROPERTY_PASSWORD  = "password";
    public static final String OPROPERTY_SMTP_HOST = "smtpHost";
    public static final String OPROPERTY_SMTP_PORT = "smtpPort";
    public static final String OPROPERTY_IMAP_HOST = "imapHost";
    public static final String OPROPERTY_IMAP_PORT = "imapPort";
    public static final String OPROPERTY_TLS_SSL   = "tlsSsl";

    public OMailSettings() {
        super(CLASS_NAME);
    }

    public OMailSettings(ODocument iDocument) {
        super(iDocument);
    }

    public OMailSettings setEmail(String email) {
        document.field(OPROPERTY_EMAIL, email);
        return this;
    }

    public String getEmail() {
        return document.field(OPROPERTY_EMAIL);
    }

    public OMailSettings setPassword(String password) {
        document.field(OPROPERTY_PASSWORD, password);
        return this;
    }

    /**
     * @return {@link String} password hash
     */
    public String getPassword() {
        return document.field(OPROPERTY_PASSWORD);
    }

    public OMailSettings setSmtpHost(String host) {
        document.field(OPROPERTY_SMTP_HOST, host);
        return this;
    }

    public String getSmtpHost() {
        return document.field(OPROPERTY_SMTP_HOST);
    }

    public OMailSettings setSmtpPort(int port) {
        document.field(OPROPERTY_SMTP_PORT, port);
        return this;
    }

    public int getSmtpPort() {
        return document.field(OPROPERTY_SMTP_PORT);
    }

    public OMailSettings setTlsSsl(boolean enable) {
        document.field(OPROPERTY_TLS_SSL, enable);
        return this;
    }

    public boolean isTlsSsl() {
        return document.field(OPROPERTY_TLS_SSL);
    }

    public String getImapHost() {
        return document.field(OPROPERTY_IMAP_HOST);
    }

    public OMailSettings setImapHost(String host) {
        document.field(OPROPERTY_IMAP_HOST, host);
        return this;
    }

    public int getImapPort() {
        return document.field(OPROPERTY_IMAP_PORT);
    }

    public OMailSettings setImapPort(int port) {
        document.field(OPROPERTY_IMAP_PORT, port);
        return this;
    }
}
