package org.orienteer.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.orienteer.core.util.CommonUtils.mapIdentifiables;

/**
 * Contains all information about E-mail for user
 */
public class OMail extends ODocumentWrapper {
    public static final String CLASS_NAME = "OMail";

    public static final String OPROPERTY_NAME     = "name";
    public static final String OPROPERTY_SUBJECT  = "subject";
    public static final String OPROPERTY_FROM     = "from";
    public static final String OPROPERTY_TEXT     = "text";
    public static final String OPROPERTY_SETTINGS = "settings";
    public static final String PROP_ATTACHMENTS   = "attachments";

    public OMail() {
        this(CLASS_NAME);
    }

    public OMail(ODocument iDocument) {
        super(iDocument);
    }

    protected OMail(String iClassName) {
        super(iClassName);
    }

    public OMail setName(String name) {
        document.field(OPROPERTY_NAME, name);
        return this;
    }

    public String getName() {
        return document.field(OPROPERTY_NAME);
    }

    public OMail setSubject(String subject) {
        document.field(OPROPERTY_SUBJECT, subject);
        return this;
    }

    public String getSubject() {
        return document.field(OPROPERTY_SUBJECT);
    }

    public OMail setFrom(String from) {
        document.field(OPROPERTY_FROM, from);
        return this;
    }

    public String getFrom() {
        return document.field(OPROPERTY_FROM);
    }

    public OMail setText(String text) {
        document.field(OPROPERTY_TEXT, text);
        return this;
    }

    public String getText() {
        return document.field(OPROPERTY_TEXT);
    }

    public OMail setMailSettings(OMailSettings settings) {
        return setMailSettings(settings.getDocument());
    }

    public OMail setMailSettings(ODocument doc) {
        document.field(OPROPERTY_SETTINGS, doc.getIdentity());
        return this;
    }

    public OMailSettings getMailSettings() {
        OIdentifiable identifiable = document.field(OPROPERTY_SETTINGS);
        ODocument doc = identifiable != null ? identifiable.getRecord() : null;
        return doc != null ? new OMailSettings(doc) : null;
    }


    public List<OMailAttachment> getAttachments() {
        return mapIdentifiables(document.field(PROP_ATTACHMENTS), OMailAttachment::new);
    }

    public OMail setAttachments(List<OMailAttachment> attachments) {
        return setAttachmentsAsDocuments(attachments.stream().map(ODocumentWrapper::getDocument).collect(Collectors.toList()));
    }

    public OMail setAttachmentsAsDocuments(List<ODocument> attachments) {
        document.field(PROP_ATTACHMENTS, attachments);
        return this;
    }
}
