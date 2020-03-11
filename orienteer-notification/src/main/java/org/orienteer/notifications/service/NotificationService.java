package org.orienteer.notifications.service;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatus;
import org.orienteer.notifications.model.ONotificationStatusHistory;
import org.orienteer.notifications.repository.ONotificationStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Singleton
public class NotificationService implements INotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  public static final int ATTEMPTS = 10;

  @Override
  @SuppressWarnings("unchecked")
  public void send(List<ONotification> notifications) {
    if (notifications == null || notifications.isEmpty()) {
      return;
    }
    ITransport<ONotification> transport = (ITransport<ONotification>) notifications.get(0).getTransport().createTransportService();

    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    notifications.forEach(notification -> {
      for (int i = 1; i <= ATTEMPTS; i++) {
        try {
          if (i == 1) {
            handleSendingNotificationStatus(db, notification);
          }
          transport.send(notification);
          handleSentNotificationStatus(db, notification);
          break;
        } catch (Exception e) {
          if (i == ATTEMPTS) {
            handleFailedNotificationStatus(db, notification);
          }
        }
      }
    });
  }

  private void handleSendingNotificationStatus(ODatabaseDocument db, ONotification notification) {
    ONotificationStatus status = ONotificationStatusRepository.getSendingStatus(db);
    updateNotificationStatus(db, notification, status);
  }

  private void handleSentNotificationStatus(ODatabaseDocument db, ONotification notification) {
    ONotificationStatus status = ONotificationStatusRepository.getSentStatus(db);
    updateNotificationStatus(db, notification, status);
  }

  private void handleFailedNotificationStatus(ODatabaseDocument db, ONotification notification) {
    ONotificationStatus status = ONotificationStatusRepository.getFailedStatus(db);
    LOG.warn("Couldn't send notification: {}", notification);
    updateNotificationStatus(db, notification, status);
  }

  private void updateNotificationStatus(ODatabaseDocument db, ONotification notification, ONotificationStatus status) {
    for (int i = 1; i <= 10; i++) {
      try {
        db.begin();
        notification.addStatusHistory(new ONotificationStatusHistory(Instant.now(), status));
        notification.setStatus(status);
        notification.save();
        db.commit();
        break;
      } catch (Exception e) {
        if (i == 10) {
           LOG.error("Couldn't save notification: {}", notification, e);
        } else {
          notification.reload();
        }
      }
    }
  }

}
