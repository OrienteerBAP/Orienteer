package org.orienteer.logger.server.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.mail.model.OMail;

import java.util.Collections;
import java.util.Set;

public class OLoggerEventMailDispatcherModel extends OLoggerEventFilteredDispatcherModel {

    public static final String CLASS_NAME = "OLoggerEventMailDispatcher";

    public static final String PROP_MAIL       = "mail";
    public static final String PROP_RECIPIENTS = "recipients";

    public OLoggerEventMailDispatcherModel() {
        super(CLASS_NAME);
    }

    public OLoggerEventMailDispatcherModel(String iClassName) {
        super(iClassName);
    }

    public OLoggerEventMailDispatcherModel(ODocument iDocument) {
        super(iDocument);
    }

    public OMail getMail() {
        ODocument mail = getMailAsDocument();
        return mail != null ? new OMail(mail) : null;
    }

    public ODocument getMailAsDocument() {
        OIdentifiable mail = document.field(PROP_MAIL);
        return mail != null ? mail.getRecord() : null;
    }

    public OLoggerEventFilteredDispatcherModel setMail(OMail mail) {
        return setMailAsDocument(mail != null ? mail.getDocument() : null);
    }

    public OLoggerEventFilteredDispatcherModel setMailAsDocument(ODocument mail) {
        document.field(PROP_MAIL, mail);
        return this;
    }

    public Set<String> getRecipients() {
        Set<String> receivers = document.field(PROP_RECIPIENTS);
        return receivers != null ? receivers : Collections.emptySet();
    }

    public OLoggerEventFilteredDispatcherModel setRecipients(Set<String> receivers) {
        document.field(PROP_RECIPIENTS, receivers);
        return this;
    }
}
