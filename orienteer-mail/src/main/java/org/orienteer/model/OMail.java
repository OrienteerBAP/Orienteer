package org.orienteer.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Contains all information about E-mail for user
 */
public class OMail extends ODocumentWrapper {
    public static final String CLASS_NAME = "OMail";
    public static final String NAME       = "name";
    public static final String SUBJECT    = "subject";
    public static final String FROM       = "from";
    public static final String TEXT       = "text";
    public static final String SETTINGS   = "settings";

    public OMail() {
        super(CLASS_NAME);
    }

    public OMail(ODocument iDocument) {
        super(iDocument);
    }

    public OMail setName(String name) {
        document.field(NAME, name);
        return this;
    }

    public String getName() {
        return document.field(NAME);
    }

    public OMail setSubject(String subject) {
        document.field(SUBJECT, subject);
        return this;
    }

    public String getSubject() {
        return document.field(SUBJECT);
    }

    public OMail setFrom(String from) {
        document.field(FROM, from);
        return this;
    }

    public String getFrom() {
        return document.field(FROM);
    }

    public OMail setText(String text) {
        document.field(TEXT, text);
        return this;
    }

    public String getText() {
        return document.field(TEXT);
    }

    public OMail setMailSettings(OMailSettings settings) {
        return setMailSettings(settings.getDocument());
    }

    public OMail setMailSettings(ODocument doc) {
        document.field(SETTINGS, doc);
        return this;
    }

    public OMailSettings getMailSettings() {
        return new OMailSettings(document.field(SETTINGS));
    }

    /**
     * Save document from admin user
     * @return {@link OMail} this instance
     */
    public OMail sudoSave() {
        DBClosure.sudoSave(this);
        return this;
    }
}
