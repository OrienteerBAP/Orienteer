package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.notifications.model.*;

/**
 * Default implementation of {@link IONotificationTransportFactory}
 */
public class ONotificationTransportFactory implements IONotificationTransportFactory {

  @Override
  public ONotificationTransport create(ODocument document) {
    Args.notNull(document, "document");

    switch (document.getSchemaClass().getName()) {
      case OMailNotificationTransport.CLASS_NAME:
        return IODocumentWrapper.get(OMailNotificationTransport.class, document);
      case OSmsNotificationTransport.CLASS_NAME:
        return IODocumentWrapper.get(OSmsNotificationTransport.class, document);
      default:
        return null;
    }
  }

}
