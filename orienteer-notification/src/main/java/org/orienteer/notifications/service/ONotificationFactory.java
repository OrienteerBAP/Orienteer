package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.notifications.model.*;

/**
 * Default implementation of {@link IONotificationFactory}
 */
public class ONotificationFactory implements IONotificationFactory {

  @Override
  public ONotification create(ODocument document) {
    Args.notNull(document, "document");

    switch (document.getSchemaClass().getName()) {
      case OMailNotification.CLASS_NAME:
        return IODocumentWrapper.get(OMailNotification.class, document);
      case OSmsNotification.CLASS_NAME:
        return IODocumentWrapper.get(OSmsNotification.class, document);
      default:
        return null;
    }
  }

}
