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
import org.orienteer.core.dao.DAO;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.Sudo;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.util.OMailUtils;
import org.orienteer.notifications.dao.ONotificationTransportDao;
import org.orienteer.notifications.model.OMailNotification;
import org.orienteer.notifications.service.INotificationService;
import org.orienteer.notifications.testenv.module.TestDataModule;

@RunWith(OrienteerTestRunner.class)
public class TestSendMailNotification {

  private OMailNotification notification;

  @Inject
  private INotificationService notificationService;

  @Inject
  private ONotificationTransportDao transportDao;

  @Before
  @Sudo
  public void init() {
    OMail mail = OMailUtils.getOMailByName(TestDataModule.MAIL_TEST)
            .orElseThrow(() -> new IllegalStateException("There is no mail with name: " + TestDataModule.MAIL_TEST));

    OPreparedMail preparedMail = new OPreparedMail(mail);
    preparedMail.addRecipient("weaxme@gmail.com");
    preparedMail.save();


    ODocument mailTransport = transportDao.findByAlias(TestDataModule.TRANSPORT_MAIL);

    if (mailTransport == null) {
      throw new IllegalStateException("There is no configured mail notification transport");
    }

    notification = DAO.create(OMailNotification.class);
    notification.fromStream(new ODocument(OMailNotification.CLASS_NAME));
    notification.setPreparedMail(preparedMail.getDocument());
    notification.setTransport(mailTransport);
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
