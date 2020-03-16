package org.orienteer.notifications.model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.service.ITransport;
import org.orienteer.notifications.service.OSmsTransport;
import org.orienteer.twilio.model.OSmsSettings;

/**
 * SMS notification transport
 */
public class OSmsNotificationTransport extends ONotificationTransport {

  public static final String CLASS_NAME = "OSmsNotificationTransport";


  public static final String PROP_SMS_SETTINGS      = "smsSettings";
  public static final String PROP_CONNECTIONS_LIMIT = "connectionsLimit";

  public OSmsNotificationTransport() {
    this(CLASS_NAME);
  }

  public OSmsNotificationTransport(String iClassName) {
    super(iClassName);
  }

  public OSmsNotificationTransport(ODocument iDocument) {
    super(iDocument);
  }

  public OSmsSettings getSettings() {
    ODocument settings = getSettingsAsDocument();
    return settings != null ? new OSmsSettings(settings) : null;
  }

  public ODocument getSettingsAsDocument() {
    OIdentifiable settings = document.field(PROP_SMS_SETTINGS);
    return settings != null ? settings.getRecord() : null;
  }

  public OSmsNotificationTransport setSettings(OSmsSettings settings) {
    return setSettingsAsDocument(settings != null ? settings.getDocument() : null);
  }

  public OSmsNotificationTransport setSettingsAsDocument(ODocument settings) {
    document.field(PROP_SMS_SETTINGS, settings);
    return this;
  }

  @Override
  public ITransport<? extends ONotification> createTransportService() {
    return new OSmsTransport(this);
  }
}
