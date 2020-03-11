package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.notifications.service.IONotificationFactory;

import java.time.Instant;
import java.util.Date;

public class ONotificationStatusHistory extends ODocumentWrapper {

  public static final String CLASS_NAME = "ONotificationStatusHistory";

  public static final String PROP_TIMESTAMP    = "timestamp";
  public static final String PROP_STATUS       = "status";
  public static final String PROP_NOTIFICATION = "notification";

  public ONotificationStatusHistory() {
    super(CLASS_NAME);
  }

  public ONotificationStatusHistory(String iClassName) {
    super(iClassName);
  }

  public ONotificationStatusHistory(ODocument iDocument) {
    super(iDocument);
  }

  public ONotificationStatusHistory(Instant timestamp, ONotificationStatus status) {
    this(timestamp, status != null ? status.getDocument() : null);
  }

  public ONotificationStatusHistory(Instant timestamp, ODocument status) {
    this();
    setTimestamp(timestamp);
    setStatusAsDocument(status);
  }

  public Instant getTimestamp() {
    Date timestamp = getTimestampAsDate();
    return timestamp != null ? timestamp.toInstant() : null;
  }

  public Date getTimestampAsDate() {
    return document.field(PROP_TIMESTAMP);
  }

  public ONotificationStatusHistory setTimestamp(Instant timestamp) {
    return setTimestampAsDate(timestamp != null ? Date.from(timestamp) : null);
  }

  public ONotificationStatusHistory setTimestampAsDate(Date timestamp) {
    document.field(PROP_TIMESTAMP, timestamp);
    return this;
  }

  public ONotificationStatus getStatus() {
    ODocument status = getStatusAsDocument();
    return status != null ? new ONotificationStatus(status) : null;
  }

  public ODocument getStatusAsDocument() {
    OIdentifiable status = document.field(PROP_STATUS);
    return status != null ? status.getRecord() : null;
  }

  public ONotificationStatusHistory setStatus(ONotificationStatus status) {
    return setStatusAsDocument(status != null ? status.getDocument() : null);
  }

  public ONotificationStatusHistory setStatusAsDocument(ODocument status) {
    document.field(PROP_STATUS, status);
    return this;
  }

  public ONotification getNotification() {
    ODocument notification = getNotificationAsDocument();
    if (notification != null) {
      return OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationFactory.class)
              .create(notification);
    }
    return null;
  }

  public ODocument getNotificationAsDocument() {
    OIdentifiable notification = document.field(PROP_NOTIFICATION);
    return notification != null ? notification.getRecord() : null;
  }

  public ONotificationStatusHistory setNotification(ONotification notification) {
    return setNotificationAsDocument(notification != null ? notification.getDocument() : null);
  }

  public ONotificationStatusHistory setNotificationAsDocument(ODocument notification) {
    document.field(PROP_NOTIFICATION, notification);
    return this;
  }
}
