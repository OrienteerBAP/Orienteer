package org.orienteer.notifications.testenv.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.mail.OMailModule;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.notifications.model.OMailNotificationTransport;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.module.ONotificationModule;
import org.orienteer.notifications.testenv.OTestNotification;
import org.orienteer.notifications.testenv.OTestNotificationTransport;

public class TestDataModule extends AbstractOrienteerModule {

  public static final String MAIL_TEST = "test-mail";

  public static final String TRANSPORT_TEST = "test-transport";
  public static final String TRANSPORT_MAIL = "test-mail-transport";

  protected TestDataModule() {
    super("test-orienteer-notifications", 1, ONotificationModule.NAME, OMailModule.NAME);
  }

  @Override
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
    OSchemaHelper helper = OSchemaHelper.bind(db);
    ODocument mailSettings = installTestMailSettings(helper);
    installOMail(helper, mailSettings);
    installTestNotificationTransports(helper, mailSettings);
    installTestNotification(helper);
    return null;
  }

  private void installTestNotification(OSchemaHelper helper) {
    helper.oClass(OTestNotification.CLASS_NAME, ONotification.CLASS_NAME);
  }

  private void installTestNotificationTransports(OSchemaHelper helper, ODocument settings) {
    helper.oClass(OTestNotificationTransport.CLASS_NAME, ONotificationTransport.CLASS_NAME)
            .getOClass();

    helper.oClass(OMailNotificationTransport.CLASS_NAME)
            .oDocument(OMailNotificationTransport.PROP_ALIAS, TRANSPORT_MAIL)
            .field(OMailNotificationTransport.PROP_NAME, CommonUtils.toMap("en", "Test Mail Transport"))
            .field(OMailNotificationTransport.PROP_MAIL_SETTINGS, settings)
            .field(OMailNotificationTransport.PROP_CONNECTIONS_LIMIT, 4)
            .saveDocument();

    helper.oClass(OTestNotificationTransport.CLASS_NAME)
            .oDocument(OTestNotificationTransport.PROP_ALIAS, TRANSPORT_TEST)
            .field(OMailNotificationTransport.PROP_NAME, CommonUtils.toMap("en", "Test Mail Transport"))
            .field(OMailNotificationTransport.PROP_CONNECTIONS_LIMIT, 4)
            .saveDocument();
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
  public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
    onInstall(app, db);
  }
}
