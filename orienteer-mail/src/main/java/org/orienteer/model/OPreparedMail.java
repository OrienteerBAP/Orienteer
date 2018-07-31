package org.orienteer.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.util.OMailUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.orienteer.core.util.CommonUtils.getFromIdentifiable;
import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

public class OPreparedMail extends ODocumentWrapper {

    public static final String CLASS_NAME = "OPreparedMail";

    public static final String PROP_NAME        = "name";
    public static final String PROP_SUBJECT     = "subject";
    public static final String PROP_FROM        = "from";
    public static final String PROP_TEXT        = "text";
    public static final String PROP_SETTINGS    = "settings";
    public static final String PROP_ATTACHMENTS = "attachments";
    public static final String PROP_RECIPIENTS  = "recipients";
    public static final String PROP_BCC         = "bcc";

    public OPreparedMail() {
        super(CLASS_NAME);
    }

    public OPreparedMail(ODocument iDocument) {
        super(iDocument);
    }

    public OPreparedMail(OMail mail) {
        this(mail, Collections.emptyMap());
    }

    public OPreparedMail(OMail mail, Map<Object, Object> macros) {
        this();
        setName(mail.getName())
                .setSubject(OMailUtils.applyMacros(mail.getSubject(), macros))
                .setText(OMailUtils.applyMacros(mail.getText(), macros))
                .setAttachments(mail.getAttachments())
                .setMailSettings(mail.getMailSettings());
    }

    public String getName() {
        return document.field(PROP_NAME);
    }

    public OPreparedMail setName(String name) {
        document.field(PROP_NAME, name);
        return this;
    }

    public String getSubject() {
        return document.field(PROP_SUBJECT);
    }

    public OPreparedMail setSubject(String subject) {
        document.field(PROP_SUBJECT, subject);
        return this;
    }

    public String getFrom() {
        return document.field(PROP_FROM);
    }

    public OPreparedMail setFrom(String from) {
        document.field(PROP_FROM, from);
        return this;
    }

    public String getText() {
        return document.field(PROP_TEXT);
    }

    public OPreparedMail setText(String text) {
        document.field(PROP_TEXT, text);
        return this;
    }

    public OMailSettings getMailSettings() {
        return getFromIdentifiable(document.field(PROP_SETTINGS), OMailSettings::new);
    }

    public OPreparedMail setMailSettings(OMailSettings settings) {
        return setMailSettingsAsDocument(settings.getDocument());
    }

    public OPreparedMail setMailSettingsAsDocument(ODocument settings) {
        document.field(PROP_SETTINGS, settings);
        return this;
    }

    public List<OMailAttachment> getAttachments() {
        return mapIdentifiables(document.field(PROP_ATTACHMENTS), OMailAttachment::new);
    }

    public OPreparedMail setAttachments(List<OMailAttachment> attachments) {
        return setAttachmentsAsDocuments(attachments.stream().map(OMailAttachment::getDocument).collect(Collectors.toList()));
    }

    public OPreparedMail setAttachmentsAsDocuments(List<ODocument> attachments) {
        document.field(PROP_ATTACHMENTS, attachments);
        return this;
    }

    public List<String> getRecipients() {
        List<String> recipients = document.field(PROP_RECIPIENTS);
        return recipients != null ? recipients : Collections.emptyList();
    }

    public OPreparedMail setRecipients(List<String> recipients) {
        document.field(PROP_RECIPIENTS, recipients);
        return this;
    }

    public List<String> getBcc() {
        List<String> bcc = document.field(PROP_BCC);
        return bcc != null ? bcc : Collections.emptyList();
    }

    public OPreparedMail setBcc(List<String> bcc) {
        document.field(PROP_BCC, bcc);
        return this;
    }
}
