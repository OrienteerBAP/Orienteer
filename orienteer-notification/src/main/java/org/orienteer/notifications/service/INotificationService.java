package org.orienteer.notifications.service;

import com.google.inject.ImplementedBy;
import org.orienteer.notifications.model.ONotification;

import java.util.Collections;
import java.util.List;

@ImplementedBy(NotificationService.class)
public interface INotificationService {

  void send(List<ONotification> notifications);

  default void send(ONotification notification) {
    send(Collections.singletonList(notification));
  }
}
