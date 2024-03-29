package org.orienteer.notifications.testenv.module;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.mail.OMailModule;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.notifications.model.IOMailNotificationTransport;
import org.orienteer.notifications.model.IONotificationDAO;
import org.orienteer.notifications.module.ONotificationModule;
import org.orienteer.notifications.service.OMailTransport;
import org.orienteer.notifications.testenv.OTestNotification;
import org.orienteer.notifications.testenv.OTestNotificationTransport;
import org.orienteer.notifications.testenv.service.OTestTransport;

public class TestDataModule extends AbstractOrienteerModule {

  public static final String MAIL_TEST = "test-mail";

  public static final String TRANSPORT_TEST = "test-transport";
  public static final String TRANSPORT_MAIL = "test-mail-transport";

  protected TestDataModule() {
    super("test-orienteer-notifications", 1, ONotificationModule.NAME, OMailModule.NAME);
  }

  @Override
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
    OSchemaHelper helper = OSchemaHelper.bind(db);
    ODocument mailSettings = installTestMailSettings(helper);
    DAO.define(OTestNotification.class, OTestNotificationTransport.class);

    installOMail(helper, mailSettings);
    installTestNotificationTransports(helper, mailSettings);

    return null;
  }


  private void installTestNotificationTransports(OSchemaHelper helper, ODocument settings) {
    IONotificationDAO transportDao = IONotificationDAO.get();

    ODocument mailTransport = transportDao.findTransportByAlias(TRANSPORT_MAIL);
    if (mailTransport == null) {
      IOMailNotificationTransport transport = DAO.create(IOMailNotificationTransport.class);
      transport.fromStream(new ODocument(IOMailNotificationTransport.CLASS_NAME));
      transport.setName(CommonUtils.toMap("en", "Test Mail Transport"));
      transport.setAlias(TRANSPORT_MAIL);
      transport.setMailSettings(settings);
      transport.setTransportClass(OMailTransport.class.getName());
      transport.save();
    }

    ODocument testTransport = transportDao.findTransportByAlias(TRANSPORT_TEST);
    if (testTransport == null) {
      OTestNotificationTransport transport = DAO.create(OTestNotificationTransport.class);
      transport.fromStream(new ODocument(OTestNotificationTransport.CLASS_NAME));
      transport.setName(CommonUtils.toMap("en", "Test Transport"));
      transport.setAlias(TRANSPORT_TEST);
      transport.setTransportClass(OTestTransport.class.getName());
      transport.save();
    }
  }

  private void installOMail(OSchemaHelper helper, ODocument settings) {
    helper.oClass(OMail.CLASS_NAME)
            .oDocument(OMail.OPROPERTY_NAME, MAIL_TEST)
            .field(OMail.OPROPERTY_FROM, "Orienteer")
            .field(OMail.OPROPERTY_SUBJECT, "Test")
            .field(OMail.OPROPERTY_SETTINGS, settings)
            .field(OMail.OPROPERTY_TEXT, "test")
            .saveDocument();
  }

  private ODocument installTestMailSettings(OSchemaHelper helper) {
    return helper.oClass(OMailSettings.CLASS_NAME)
            .oDocument(OMailSettings.OPROPERTY_EMAIL, "test")
            .field(OMailSettings.OPROPERTY_PASSWORD, "test")
            .field(OMailSettings.OPROPERTY_IMAP_HOST, "imap.gmail.com")
            .field(OMailSettings.OPROPERTY_IMAP_PORT, 993)
            .field(OMailSettings.OPROPERTY_SMTP_HOST, "smtp.gmail.com")
            .field(OMailSettings.OPROPERTY_SMTP_PORT, 587)
            .field(OMailSettings.OPROPERTY_TLS_SSL, true)
            .saveDocument()
            .getODocument();
  }

  @Override
  public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
    onInstall(app, db);
  }
}
