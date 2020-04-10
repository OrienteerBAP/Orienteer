package org.orienteer.notifications.testenv.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAO;
import org.orienteer.notifications.model.IONotification;
import org.orienteer.notifications.service.ONotificationFactory;
import org.orienteer.notifications.testenv.OTestNotification;

public class ONotificationTestFactory extends ONotificationFactory {

  @Override
  public IONotification create(ODocument document) {
    if (document.getSchemaClass().getName().equalsIgnoreCase(OTestNotification.CLASS_NAME)) {
      IONotification notification = DAO.create(OTestNotification.class);
      notification.fromStream(document);
      return notification;
    }
    return super.create(document);
  }
}
