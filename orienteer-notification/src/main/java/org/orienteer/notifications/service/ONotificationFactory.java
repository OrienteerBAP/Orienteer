package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.model.ONotification;

public class ONotificationFactory implements IONotificationFactory {

  @Override
  public ONotification create(ODocument document) {
    Args.notNull(document, "document");

    switch (document.getSchemaClass().getName()) {
      case OMailNotification.CLASS_NAME:
        return new OMailNotification(document);
      default:
        return null;
    }
  }

}
