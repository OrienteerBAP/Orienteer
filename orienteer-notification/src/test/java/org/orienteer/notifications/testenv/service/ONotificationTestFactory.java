package org.orienteer.notifications.testenv.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.service.ONotificationFactory;
import org.orienteer.notifications.testenv.OTestNotification;

public class ONotificationTestFactory extends ONotificationFactory {

  @Override
  public ONotification create(ODocument document) {
    if (document.getSchemaClass().getName().equalsIgnoreCase(OTestNotification.CLASS_NAME)) {
      return IODocumentWrapper.get(OTestNotification.class, document);
    }
    return super.create(document);
  }
}
