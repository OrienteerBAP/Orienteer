package org.orienteer.notifications.test;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.util.OMailUtils;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatusHistory;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.repository.ONotificationStatusRepository;
import org.orienteer.notifications.repository.ONotificationTransportRepository;
import org.orienteer.notifications.scheduler.ONotificationScheduler;
import org.orienteer.notifications.task.ONotificationSendTask;
import org.orienteer.notifications.testenv.module.TestDataModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrienteerTestRunner.class)
public class TestSendNotificationTask {

  private static final Logger LOG = LoggerFactory.getLogger(TestSendNotificationTask.class);

  public static final int NOTIFICATIONS = 500;

  private List<ONotification> notifications;

  @Before
  @Sudo
  public void init() {
    ONotificationScheduler.stopAll();

    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    ONotificationTransport transport = ONotificationTransportRepository.getTransportByAlias(db, TestDataModule.TRANSPORT_MAIL)
            .orElseThrow(IllegalStateException::new);

    OMail mail = OMailUtils.getOMailByName(TestDataModule.MAIL_TEST)
            .orElseThrow(IllegalStateException::new);

    notifications = new LinkedList<>();

    for (int i = 0; i < NOTIFICATIONS; i++) {
      db.begin();
      OPreparedMail preparedMail = new OPreparedMail(mail);
      ONotification notification = new OMailNotification(preparedMail, transport);
      preparedMail.addRecipient("vetalgonchar@gmail.com");
      preparedMail.save();
      notification.save();
      notifications.add(notification);
      db.commit();
    }
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    notifications.forEach(notification -> {
      for (int i = 0; i < 3; i++) {
        try {
          db.begin();
          db.delete(notification.getDocument());
          db.commit();
          break;
        } catch (Exception e) {
          notification.reload();
        }
      }
    });
  }


  @Test
  @Sudo
  public void testScheduler() throws InterruptedException {
    ONotificationSendTask task = new ONotificationSendTask();
    task.run();

    long countOfSentNotificaitons = notifications.stream()
            .filter(notification -> {
              notification.reload();
              return new LinkedList<>(notification.getStatusHistories()).getLast().getStatus().equals(ONotificationStatusRepository.getSentStatus());
            }).count();

    LOG.info("all notifications: {} sent notificaitons: {}", notifications.size(), countOfSentNotificaitons);

    notifications.forEach(this::assertNotificationLifecycle);
  }

  private void assertNotificationLifecycle(ONotification notification) {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    notification.reload();

    LinkedList<ONotificationStatusHistory> statusHistories = new LinkedList<>(notification.getStatusHistories());
    assertEquals(3, statusHistories.size());

    ONotificationStatusHistory pendingHistory = statusHistories.pop();
    assertNotNull(pendingHistory);
    assertEquals(pendingHistory.getStatus(), ONotificationStatusRepository.getPendingStatus(db));

    ONotificationStatusHistory sendingHistory = statusHistories.pop();
    assertNotNull(sendingHistory);
    assertEquals(sendingHistory.getStatus(), ONotificationStatusRepository.getSendingStatus(db));

    ONotificationStatusHistory sentHistory = statusHistories.pop();
    assertNotNull(sentHistory);
    assertEquals(sentHistory.getStatus(), ONotificationStatusRepository.getSentStatus(db));

  }

}
