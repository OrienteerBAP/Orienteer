package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.notifications.service.ITransport;
import org.orienteer.notifications.service.OMailTransport;

/**
 * Mail notification transport
 */
public class OMailNotificationTransport extends ONotificationTransport {
  public static final String CLASS_NAME = "OMailNotificationTransport";

  public static final String PROP_MAIL_SETTINGS     = "mailSettings";
  public static final String PROP_CONNECTIONS_LIMIT = "connectionsLimit";

  public OMailNotificationTransport() {
    this(CLASS_NAME);
  }

  public OMailNotificationTransport(String iClassName) {
    super(iClassName);
  }

  public OMailNotificationTransport(ODocument iDocument) {
    super(iDocument);
  }

  @Override
  public ITransport<OMailNotification> createTransportService() {
    return new OMailTransport(this);
  }

  public OMailSettings getMailSettings() {
    ODocument settings = getMailSettingsAsDocument();
    return settings != null ? new OMailSettings(settings) : null;
  }

  public ODocument getMailSettingsAsDocument() {
    OIdentifiable settings = document.field(PROP_MAIL_SETTINGS);
    return settings != null ? settings.getRecord() : null;
  }

  public OMailNotificationTransport setMailSettings(OMailSettings settings) {
    return setMailSettingsAsDocument(settings != null ? settings.getDocument() : null);
  }

  public OMailNotificationTransport setMailSettingsAsDocument(ODocument settings) {
    document.field(PROP_MAIL_SETTINGS, settings);
    return this;
  }

  public int getConnectionsLimit() {
    Integer limit = document.field(PROP_CONNECTIONS_LIMIT);
    return limit != null ? limit : 1;
  }

  public OMailNotificationTransport setConnectionsLimit(int limit) {
    document.field(PROP_CONNECTIONS_LIMIT, limit);
    return this;
  }
}
