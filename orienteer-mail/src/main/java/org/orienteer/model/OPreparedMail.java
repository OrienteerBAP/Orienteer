package org.orienteer.model;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.util.OMailUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.orienteer.core.util.CommonUtils.getFromIdentifiable;
import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

/**
 * Prepared mail for send it via {@link org.orienteer.service.IOMailService}
 * Contains information about mail with applied macros and adjusted recipients, bcc.
 * Can be used for analyze generated and send mails
 */
public class OPreparedMail extends ODocumentWrapper {

    /**
     * OrientDB class name
     */
    public static final String CLASS_NAME = "OPreparedMail";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Name of prepared mail
     */
    public static final String PROP_NAME        = "name";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Subject of prepared mail
     */
    public static final String PROP_SUBJECT     = "subject";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Mail from
     */
    public static final String PROP_FROM        = "from";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#STRING}
     * Mail text content
     */
    public static final String PROP_TEXT        = "text";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#LINK}
     * Link to {@link OMailSettings} which was used for send this mail
     */
    public static final String PROP_SETTINGS    = "settings";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#LINKLIST}
     * List of link to attachments which was send in this mail
     */
    public static final String PROP_ATTACHMENTS = "attachments";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#EMBEDDEDLIST}
     * String list of recipients for this mail
     */
    public static final String PROP_RECIPIENTS  = "recipients";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#EMBEDDEDLIST}
     * String list of bcc for this mail
     */
    public static final String PROP_BCC         = "bcc";

    /**
     * {@link com.orientechnologies.orient.core.metadata.schema.OType#LINK}
     * Link to mail template
     */
    public static final String PROP_MAIL        = "mail";

    public OPreparedMail() {
        super(CLASS_NAME);
    }

    public OPreparedMail(ODocument iDocument) {
        super(iDocument);
    }

    public OPreparedMail(OMail mail) {
        this(mail, Collections.emptyMap());
    }

    public OPreparedMail(OMail mail, Map<String, Object> macros) {
        this();
        setName(mail.getName())
                .setFrom(OMailUtils.applyMacros(mail.getFrom(), macros))
                .setSubject(OMailUtils.applyMacros(mail.getSubject(), macros))
                .setText(OMailUtils.applyMacros(mail.getText(), macros))
                .setAttachments(mail.getAttachments())
                .setMailSettings(mail.getMailSettings())
                .setMailTemplate(mail);
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
        return getFromIdentifiable(document.field(PROP_SETTINGS), OMailSettings::new).orElse(null);
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

    public OPreparedMail addRecipient(String recipient) {
        List<String> recipients = new LinkedList<>(getRecipients());
        recipients.add(recipient);
        return setRecipients(recipients);
    }

    public OPreparedMail removeRecipient(String recipient) {
        List<String> recipients = new LinkedList<>(getRecipients());
        if (recipients.remove(recipient)) {
            setRecipients(recipients);
        }
        return this;
    }

    public OPreparedMail setRecipients(List<String> recipients) {
        document.field(PROP_RECIPIENTS, recipients);
        return this;
    }

    public List<String> getBcc() {
        List<String> bcc = document.field(PROP_BCC);
        return bcc != null ? bcc : Collections.emptyList();
    }

    public OPreparedMail addBcc(String bcc) {
        List<String> bccList = new LinkedList<>(getBcc());
        bccList.add(bcc);
        return setBcc(bccList);
    }

    public OPreparedMail removeBcc(String bcc) {
        List<String> bccList = new LinkedList<>(getBcc());
        if (bccList.remove(bcc)) {
            setBcc(bccList);
        }
        return this;
    }

    public OPreparedMail setBcc(List<String> bcc) {
        document.field(PROP_BCC, bcc);
        return this;
    }

    public OMail getMailTemplate() {
        return getFromIdentifiable(document.field(PROP_MAIL), OMail::new).orElse(null);
    }

    public OPreparedMail setMailTemplate(OMail mailTemplate) {
        return setMailTemplateAsDocument(mailTemplate.getDocument());
    }

    public OPreparedMail setMailTemplateAsDocument(ODocument mail) {
        document.field(PROP_MAIL, mail);
        return this;
    }
}
