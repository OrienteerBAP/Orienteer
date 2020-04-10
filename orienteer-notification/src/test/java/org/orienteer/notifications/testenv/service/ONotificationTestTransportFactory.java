package org.orienteer.notifications.testenv.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAO;
import org.orienteer.notifications.model.IONotificationTransport;
import org.orienteer.notifications.service.ONotificationTransportFactory;
import org.orienteer.notifications.testenv.OTestNotificationTransport;

public class ONotificationTestTransportFactory extends ONotificationTransportFactory {

  @Override
  public IONotificationTransport create(ODocument document) {
    if (document.getSchemaClass().getName().equalsIgnoreCase(OTestNotificationTransport.CLASS_NAME)) {
      IONotificationTransport transport = DAO.create(IONotificationTransport.class);
      transport.fromStream(document);
      return transport;
    }
    return super.create(document);
  }
}
