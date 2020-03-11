package org.orienteer.notifications.test;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatusHistory;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.repository.ONotificationStatusRepository;
import org.orienteer.notifications.repository.ONotificationTransportRepository;
import org.orienteer.notifications.service.INotificationService;
import org.orienteer.notifications.testenv.OTestNotification;
import org.orienteer.notifications.testenv.module.TestDataModule;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
public class TestNotificationLifecycle {

  private ONotification testNotification;

  @Inject
  private INotificationService notificationService;

  @Before
  @Sudo
  public void init() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    ONotificationTransport testNotificationTransport = ONotificationTransportRepository.getTransportByAlias(db, TestDataModule.TRANSPORT_TEST)
            .orElseThrow(() -> new IllegalStateException("There is no configured test notification transport"));

    testNotification = new OTestNotification();
    testNotification.setTransport(testNotificationTransport);
    testNotification.save();
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    for (int i = 0; i < 10; i++) {
      try {
        db.delete(testNotification.getDocument());
        break;
      } catch (Exception e) {
        testNotification.reload();
      }
    }

  }

  @Test
  @Sudo
  public void testSuccessfulLifecycle() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    notificationService.send(testNotification);
    testNotification.reload();

    List<ONotificationStatusHistory> statusHistories = testNotification.getStatusHistories();
    assertEquals(3, statusHistories.size());

    ONotificationStatusHistory pendingHistory = statusHistories.get(0);
    assertNotNull(pendingHistory);
    assertEquals(pendingHistory.getStatus(), ONotificationStatusRepository.getPendingStatus(db));

    ONotificationStatusHistory sendingHistory = statusHistories.get(1);
    assertNotNull(sendingHistory);
    assertEquals(sendingHistory.getStatus(), ONotificationStatusRepository.getSendingStatus(db));

    ONotificationStatusHistory sentHistory = statusHistories.get(2);
    assertNotNull(sentHistory);
    assertEquals(sentHistory.getStatus(), ONotificationStatusRepository.getSentStatus(db));
  }

}
