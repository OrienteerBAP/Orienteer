package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.notifications.model.OMailNotificationTransport;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.model.OSmsNotificationTransport;

/**
 * Default implementation of {@link IONotificationTransportFactory}
 */
public class ONotificationTransportFactory implements IONotificationTransportFactory {

  @Override
  public ONotificationTransport create(ODocument document) {
    Args.notNull(document, "document");

    switch (document.getSchemaClass().getName()) {
      case OMailNotificationTransport.CLASS_NAME:
        return new OMailNotificationTransport(document);
      case OSmsNotificationTransport.CLASS_NAME:
        return new OSmsNotificationTransport(document);
      default:
        return null;
    }
  }

}
