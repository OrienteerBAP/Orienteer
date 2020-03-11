package org.orienteer.notifications.test;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.util.OMailUtils;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.repository.ONotificationTransportRepository;
import org.orienteer.notifications.service.INotificationService;
import org.orienteer.notifications.testenv.module.TestDataModule;

@RunWith(OrienteerTestRunner.class)
public class TestSendMailNotification {

  private OMailNotification notification;

  @Inject
  private INotificationService notificationService;


  @Before
  @Sudo
  public void init() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    OMail mail = OMailUtils.getOMailByName(TestDataModule.MAIL_TEST)
            .orElseThrow(() -> new IllegalStateException("There is no mail with name: " + TestDataModule.MAIL_TEST));

    OPreparedMail preparedMail = new OPreparedMail(mail);
    preparedMail.addRecipient("weaxme@gmail.com");
    preparedMail.save();

    ONotificationTransport transport = ONotificationTransportRepository.getTransportByAlias(db, TestDataModule.TRANSPORT_MAIL)
            .orElseThrow(() -> new IllegalStateException("There is no configured mail notification transport"));

    notification = new OMailNotification();
    notification.setPreparedMail(preparedMail);
    notification.setTransport(transport);
    notification.save();
  }

  @After
  @Sudo
  public void destroy() {
    ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().get();
    for (int i = 0; i < 10; i++) {
      try {
        db.delete(notification.getDocument());
        break;
      } catch (Exception e) {
        notification.reload();
      }
    }
  }

  @Test
  @Sudo
  @Ignore
  public void testSendMailNotification() {
    notificationService.send(notification);
  }
}
