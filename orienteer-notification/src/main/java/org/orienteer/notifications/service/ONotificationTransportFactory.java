package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.http.util.Args;
import org.orienteer.core.dao.DAO;
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
    ONotificationTransport transport = null;

    switch (document.getSchemaClass().getName()) {
      case OMailNotificationTransport.CLASS_NAME:
        transport = DAO.create(OMailNotificationTransport.class);
        break;
      case OSmsNotificationTransport.CLASS_NAME:
        transport = DAO.create(OSmsNotificationTransport.class);
        break;
    }

    if (transport != null) {
      transport.fromStream(document);
    }

    return transport;
  }

}
