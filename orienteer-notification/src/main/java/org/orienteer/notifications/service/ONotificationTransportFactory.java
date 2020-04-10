package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.core.dao.DAO;
import org.orienteer.notifications.model.IOMailNotificationTransport;
import org.orienteer.notifications.model.IONotificationTransport;
import org.orienteer.notifications.model.IOSmsNotificationTransport;

/**
 * Default implementation of {@link IONotificationTransportFactory}
 */
public class ONotificationTransportFactory implements IONotificationTransportFactory {

  @Override
  public IONotificationTransport create(ODocument document) {
    Args.notNull(document, "document");
    IONotificationTransport transport = null;

    switch (document.getSchemaClass().getName()) {
      case IOMailNotificationTransport.CLASS_NAME:
        transport = DAO.create(IOMailNotificationTransport.class);
        break;
      case IOSmsNotificationTransport.CLASS_NAME:
        transport = DAO.create(IOSmsNotificationTransport.class);
        break;
    }

    if (transport != null) {
      transport.fromStream(document);
    }

    return transport;
  }

}
