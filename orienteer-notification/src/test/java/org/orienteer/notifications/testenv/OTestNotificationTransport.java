package org.orienteer.notifications.testenv;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.service.ITransport;
import org.orienteer.notifications.testenv.service.OTestTransport;

public class OTestNotificationTransport extends ONotificationTransport {

  public static final String CLASS_NAME = "OTestNotificationTransport";

  public OTestNotificationTransport() {
    this(CLASS_NAME);
  }

  public OTestNotificationTransport(String iClassName) {
    super(iClassName);
  }

  public OTestNotificationTransport(ODocument iDocument) {
    super(iDocument);
  }

  @Override
  public ITransport<? extends ONotification> createTransportService() {
    return new OTestTransport();
  }
}
