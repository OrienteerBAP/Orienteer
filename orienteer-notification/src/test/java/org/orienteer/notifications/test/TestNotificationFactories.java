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
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.repository.ONotificationTransportRepository;
import org.orienteer.notifications.service.IONotificationFactory;
import org.orienteer.notifications.service.IONotificationTransportFactory;
import org.orienteer.notifications.testenv.OTestNotification;
import org.orienteer.notifications.testenv.module.TestDataModule;

import static org.junit.Assert.assertEquals;

@RunWith(OrienteerTestRunner.class)
public class TestNotificationFactories {

  private ONotification testNotification;
  private ONotificationTransport testNotificationTransport;

  @Inject
  private IONotificationFactory notificationFactory;

  @Inject
  private IONotificationTransportFactory transportFactory;

  @Before
  @Sudo
  public void init() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();

    testNotificationTransport = ONotificationTransportRepository.getTransportByAlias(db, TestDataModule.TRANSPORT_TEST)
      .orElseThrow(() -> new IllegalStateException("There is no configured test notification transport"));

    testNotification = new OTestNotification();
    testNotification.setTransport(testNotificationTransport);
    testNotification.save();
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    db.delete(testNotification.getDocument());
  }


  @Test
  @Sudo
  public void testNotificationFactory() {
    ONotification createdNotification = notificationFactory.create(testNotification.getDocument());
    assertEquals(testNotification.getClass(), createdNotification.getClass());
  }

  @Test
  @Sudo
  public void testNotificationTransportFactory() {
    ONotificationTransport createdTransport = transportFactory.create(testNotificationTransport.getDocument());
    assertEquals(testNotificationTransport.getClass(), createdTransport.getClass());
  }

}
