package org.orienteer.notifications.test;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.util.OMailUtils;
import org.orienteer.notifications.dao.ONotificationStatusDao;
import org.orienteer.notifications.dao.ONotificationTransportDao;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatusHistory;
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

  @Inject
  private ONotificationTransportDao transportDao;

  @Inject
  private ONotificationStatusDao statusDao;

  @Before
  @Sudo
  public void init() {
    ONotificationScheduler.stopAll();

    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    ODocument mailTransport = transportDao.findByAlias(TestDataModule.TRANSPORT_MAIL);

    if (mailTransport == null) {
      throw new IllegalStateException("There is no transport with alias: " + TestDataModule.TRANSPORT_MAIL);
    }

    OMail mail = OMailUtils.getOMailByName(TestDataModule.MAIL_TEST)
            .orElseThrow(IllegalStateException::new);

    notifications = new LinkedList<>();

    for (int i = 0; i < NOTIFICATIONS; i++) {
      db.begin();
      OPreparedMail preparedMail = new OPreparedMail(mail);
      OMailNotification notification = IODocumentWrapper.get(OMailNotification.class, new ODocument(OMailNotification.CLASS_NAME));
      notification.setTransport(mailTransport);
      notification.setPreparedMail(preparedMail.getDocument());
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
              return new LinkedList<>(notification.getStatusHistories()).getLast().equals(statusDao.getSent());
            }).count();

    LOG.info("all notifications: {} sent notificaitons: {}", notifications.size(), countOfSentNotificaitons);

    notifications.forEach(this::assertNotificationLifecycle);
  }

  private void assertNotificationLifecycle(ONotification notification) {
    notification.reload();

    ONotificationStatusHistory statusHistory = IODocumentWrapper.get(ONotificationStatusHistory.class);

    LinkedList<ODocument> statusHistories = new LinkedList<>(notification.getStatusHistories());
    assertEquals(3, statusHistories.size());

    ODocument pendingHistory = statusHistories.pop();
    assertNotNull(pendingHistory);
    statusHistory.fromStream(pendingHistory);
    assertEquals(statusHistory.getStatus(), statusDao.getPending());

    ODocument sendingHistory = statusHistories.pop();
    assertNotNull(sendingHistory);
    statusHistory.fromStream(sendingHistory);
    assertEquals(statusHistory.getStatus(), statusDao.getSending());

    ODocument sentHistory = statusHistories.pop();
    assertNotNull(sentHistory);
    statusHistory.fromStream(sentHistory);
    assertEquals(statusHistory.getStatus(), statusDao.getSent());

  }

}
