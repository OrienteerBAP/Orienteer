package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.core.dao.DAO;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.OSmsNotification;

/**
 * Default implementation of {@link IONotificationFactory}
 */
public class ONotificationFactory implements IONotificationFactory {

  @Override
  public ONotification create(ODocument document) {
    Args.notNull(document, "document");
    ONotification notification = null;

    switch (document.getSchemaClass().getName()) {
      case OMailNotification.CLASS_NAME:
        notification = DAO.create(OMailNotification.class);
        break;
      case OSmsNotification.CLASS_NAME:
        notification = DAO.create(OSmsNotification.class);
        break;
    }

    if (notification != null) {
      notification.fromStream(document);
    }

    return notification;
  }

}
