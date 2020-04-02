package org.orienteer.notifications.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.notifications.dao.ONotificationStatusDao;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatusHistory;
import org.orienteer.notifications.model.ONotificationTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link INotificationService}
 */
@Singleton
public class NotificationService implements INotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  public static final int ATTEMPTS = 10;

  private final OTransportPool transportPool = new OTransportPool();

  @Inject
  private ONotificationStatusDao notificationStatusDao;

  @Override
  @SuppressWarnings("unchecked")
  public void send(List<ODocument> notifications) {
    if (notifications == null || notifications.isEmpty()) {
      return;
    }
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    ONotification notification = IODocumentWrapper.get(ONotification.class);
    ONotificationTransport transportWrapper = IODocumentWrapper.get(ONotificationTransport.class);

    notifications.forEach(notificationDoc -> {
      notification.fromStream(notificationDoc);
      transportWrapper.fromStream(notification.getTransport());
      ITransport transport = transportPool.acquire(transportWrapper.getAlias(), transportWrapper::createTransportService);

      for (int i = 1; i <= ATTEMPTS; i++) {
        try {
          if (i == 1) {
            handleSendingNotificationStatus(db, notification);
          }
          LOG.info("Send notification: {} {}", Thread.currentThread().getName(), notification.getDocument());
          transport.send(notificationDoc);
          handleSentNotificationStatus(db, notification);
          transportPool.release(transportWrapper.getAlias(), transport);
          break;
        } catch (Exception e) {
          if (i == ATTEMPTS) {
            handleFailedNotificationStatus(db, notification, e);
          }
        }
      }
    });
  }

  private void handleSendingNotificationStatus(ODatabaseDocument db, ONotification notification) {
    ODocument status = notificationStatusDao.getSending();
    updateNotificationStatus(db, notification, status);
  }

  private void handleSentNotificationStatus(ODatabaseDocument db, ONotification notification) {
    ODocument status = notificationStatusDao.getSent();
    updateNotificationStatus(db, notification, status);
  }

  private void handleFailedNotificationStatus(ODatabaseDocument db, ONotification notification, Exception e) {
    ODocument status = notificationStatusDao.getFailed();
    LOG.warn("Couldn't send notification: {}", notification.getDocument(), e);
    updateNotificationStatus(db, notification, status);
  }

  private void updateNotificationStatus(ODatabaseDocument db, ONotification notification, ODocument status) {
    for (int i = 1; i <= 10; i++) {
      try {
        db.begin();
        notification.addStatusHistory(ONotificationStatusHistory.create(new Date(), status));
        notification.setStatus(status);
        notification.save();
        db.commit();
        break;
      } catch (Exception e) {
        if (i == 10) {
           LOG.error("Couldn't save notification: {}", notification.getDocument(), e);
        } else {
          notification.reload();
        }
      }
    }
  }

}
