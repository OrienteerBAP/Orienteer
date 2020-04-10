package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.core.dao.DAO;
import org.orienteer.notifications.model.IOMailNotification;
import org.orienteer.notifications.model.IONotification;
import org.orienteer.notifications.model.IOSmsNotification;

/**
 * Default implementation of {@link IONotificationFactory}
 */
public class ONotificationFactory implements IONotificationFactory {

  @Override
  public IONotification create(ODocument document) {
    Args.notNull(document, "document");
    IONotification notification = null;

    switch (document.getSchemaClass().getName()) {
      case IOMailNotification.CLASS_NAME:
        notification = DAO.create(IOMailNotification.class);
        break;
      case IOSmsNotification.CLASS_NAME:
        notification = DAO.create(IOSmsNotification.class);
        break;
    }

    if (notification != null) {
      notification.fromStream(document);
    }

    return notification;
  }

}
