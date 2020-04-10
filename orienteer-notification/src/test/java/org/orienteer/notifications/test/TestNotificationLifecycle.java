package org.orienteer.notifications.test;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.dao.DAO;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.notifications.model.IONotification;
import org.orienteer.notifications.model.IONotificationDAO;
import org.orienteer.notifications.model.IONotificationStatusHistory;
import org.orienteer.notifications.service.INotificationService;
import org.orienteer.notifications.testenv.OTestNotification;
import org.orienteer.notifications.testenv.module.TestDataModule;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrienteerTestRunner.class)
public class TestNotificationLifecycle {

  private IONotification testNotification;

  @Inject
  private INotificationService notificationService;

  @Inject
  private IONotificationDAO notificationDAO;


  @Before
  @Sudo
  public void init() {
    ODocument testTransport = notificationDAO.findTransportByAlias(TestDataModule.TRANSPORT_TEST);

    if (testTransport == null) {
      throw new IllegalStateException("There is no configured test notification transport");
    }

    testNotification = DAO.create(OTestNotification.class);
    testNotification.setTransport(testTransport);
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
    notificationService.send(testNotification.getDocument());
    testNotification.reload();

    IONotificationStatusHistory statusHistory = DAO.create(IONotificationStatusHistory.class);
    statusHistory.fromStream(new ODocument(OTestNotification.CLASS_NAME));

    List<ODocument> statusHistories = testNotification.getStatusHistories();
    assertEquals(3, statusHistories.size());

    assertNotNull(statusHistories.get(0));
    statusHistory.fromStream(statusHistories.get(0));
    assertEquals(statusHistory.getStatus(), notificationDAO.getPendingStatus());

    assertNotNull(statusHistories.get(1));
    statusHistory.fromStream(statusHistories.get(1));
    assertEquals(statusHistory.getStatus(), notificationDAO.getSendingStatus());

    assertNotNull(statusHistories.get(2));
    statusHistory.fromStream(statusHistories.get(2));
    assertEquals(statusHistory.getStatus(), notificationDAO.getSentStatus());
  }

}
