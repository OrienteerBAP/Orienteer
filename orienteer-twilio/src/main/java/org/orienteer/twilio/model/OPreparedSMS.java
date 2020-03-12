package org.orienteer.twilio.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.twilio.util.OTwilioUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OPreparedSMS extends ODocumentWrapper {

  public static final String CLASS_NAME = "OPreparedSMS";

  public static final String PROP_ID = "id";
  public static final String PROP_SMS = "sms";
  public static final String PROP_TEXT = "text";
  public static final String PROP_ATTACHMENTS = "attachments";
  public static final String PROP_RECIPIENT = "recipient";
  public static final String PROP_SUCCESS = "success";
  public static final String PROP_TIMESTAMP = "timestamp";

  public OPreparedSMS() {
    this(CLASS_NAME);
  }

  public OPreparedSMS(String iClassName) {
    super(iClassName);
  }

  public OPreparedSMS(ODocument iDocument) {
    super(iDocument);
  }

  public OPreparedSMS(OSMS sms, String recipient, Map<String, Object> macros) {
    this(sms, recipient, macros, null);
  }

  public OPreparedSMS(OSMS sms, String recipient, Map<String, Object> macros, List<String> attachments) {
    this();
    setSMS(sms);
    setRecipient(recipient);
    setText(OTwilioUtils.applyMacros(sms.getText(), macros));
    setAttachments(attachments);
  }

  public String getId() {
    return document.field(PROP_ID);
  }

  public OPreparedSMS setId(String id) {
    document.field(PROP_ID, id);
    return this;
  }

  public String getText() {
    return document.field(PROP_TEXT);
  }

  public OPreparedSMS setText(String text) {
    document.field(PROP_TEXT, text);
    return this;
  }

  public OSMS getSMS() {
    ODocument sms = getSMSAsDocument();
    return sms != null ? new OSMS(sms) : null;
  }

  public ODocument getSMSAsDocument() {
    OIdentifiable sms = document.field(PROP_SMS);
    return sms != null ? sms.getRecord() : null;
  }

  public OPreparedSMS setSMS(OSMS sms) {
    return setSMSAsDocument(sms != null ? sms.getDocument() : null);
  }

  public OPreparedSMS setSMSAsDocument(ODocument sms) {
    document.field(PROP_SMS, sms);
    return this;
  }

  public String getRecipient() {
    return document.field(PROP_RECIPIENT);
  }

  public OPreparedSMS setRecipient(String recipient) {
    document.field(PROP_RECIPIENT, recipient);
    return this;
  }

  public Instant getTimestamp() {
    Date date = getTimestampAsDate();
    return date != null ? date.toInstant() : null;
  }

  public Date getTimestampAsDate() {
    return document.field(PROP_TIMESTAMP);
  }

  public OPreparedSMS setTimestamp(Instant timestamp) {
    return setTimestampAsDate(timestamp != null ? Date.from(timestamp) : null);
  }

  public OPreparedSMS setTimestampAsDate(Date timestamp) {
    document.field(PROP_TIMESTAMP, timestamp);
    return this;
  }


  public boolean isSuccess() {
    Boolean success = document.field(PROP_SUCCESS);
    return success != null && success;
  }

  public OPreparedSMS setSuccess(boolean success) {
    document.field(PROP_SUCCESS, success);
    return this;
  }

  public List<String> getAttachments() {
    List<String> attachments = document.field(PROP_ATTACHMENTS);
    return attachments != null ? attachments : Collections.emptyList();
  }

  public OPreparedSMS setAttachments(List<String> attachments) {
    document.field(PROP_ATTACHMENTS, attachments);
    return this;
  }
}
