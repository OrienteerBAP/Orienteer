package org.orienteer.notifications.test;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.notifications.dao.ONotificationTransportDao;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.service.IONotificationFactory;
import org.orienteer.notifications.service.IONotificationTransportFactory;
import org.orienteer.notifications.testenv.OTestNotification;
import org.orienteer.notifications.testenv.OTestNotificationTransport;
import org.orienteer.notifications.testenv.module.TestDataModule;

import static org.junit.Assert.assertEquals;

@RunWith(OrienteerTestRunner.class)
public class TestNotificationFactories {

  private OTestNotification testNotification;
  private OTestNotificationTransport testNotificationTransport;

  @Inject
  private IONotificationFactory notificationFactory;

  @Inject
  private IONotificationTransportFactory transportFactory;

  @Inject
  private ONotificationTransportDao transportDao;

  @Before
  @Sudo
  public void init() {
    ODocument testTransport = transportDao.findByAlias(TestDataModule.TRANSPORT_TEST);

    if (testTransport == null) {
      throw new IllegalStateException("There is no configured test notification transport");
    }

    testNotificationTransport = IODocumentWrapper.get(OTestNotificationTransport.class, testTransport);

    testNotification = IODocumentWrapper.get(OTestNotification.class, new ODocument(OTestNotification.CLASS_NAME));
    testNotification.setTransport(testNotificationTransport.getDocument());
    testNotification.save();
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    for (int i = 1; i <= 10; i++) {
      try {
        db.begin();
        db.delete(testNotification.getDocument());
        db.commit();
        break;
      } catch (Exception e) {
        if (i == 10) {
          throw new IllegalStateException(e);
        }
      }
    }
  }


  @Test
  @Sudo
  @Ignore
  public void testNotificationFactory() {
    ONotification createdNotification = notificationFactory.create(testNotification.getDocument());
    assertEquals(testNotification.getClass(), createdNotification.getClass());
  }

  @Test
  @Sudo
  @Ignore
  public void testNotificationTransportFactory() {
    ONotificationTransport createdTransport = transportFactory.create(testNotificationTransport.getDocument());
    assertEquals(testNotificationTransport.getClass(), createdTransport.getClass());
  }

}
