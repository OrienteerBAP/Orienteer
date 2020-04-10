package org.orienteer.notifications.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.IONotification;

import java.util.Collections;
import java.util.List;

/**
 * Notification Service interface
 */
@ImplementedBy(NotificationService.class)
public interface INotificationService {

  void send(List<ODocument> notifications);

  default void send(ODocument notification) {
    send(Collections.singletonList(notification));
  }

  default void send(IONotification notification) {
    send(notification.getDocument());
  }

}
