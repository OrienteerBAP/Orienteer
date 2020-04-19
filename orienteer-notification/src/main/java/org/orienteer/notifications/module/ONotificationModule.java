package org.orienteer.notifications.module;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.mail.OMailModule;
import org.orienteer.notifications.hook.ONotificationHook;
import org.orienteer.notifications.model.*;
import org.orienteer.notifications.scheduler.ONotificationScheduler;
import org.orienteer.notifications.task.ONotificationSendTask;
import org.orienteer.twilio.module.OTwilioModule;

import java.util.List;

/**
 * Module for install data model for 'orienteer-notification' module
 */
public class ONotificationModule extends AbstractOrienteerModule {

  public static final String NAME = "orienteer-notification";
  public static final int VERSION = 2;

  protected ONotificationModule() {
    super(NAME, VERSION, OMailModule.NAME, OTwilioModule.NAME);
  }

  @Override
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
    OSchemaHelper helper = OSchemaHelper.bind(db);

    DAO.describe(helper, IONotification.class, IONotificationStatus.class, IONotificationTransport.class, IONotificationStatusHistory.class);
    DAO.describe(helper, IOMailNotification.class, IOMailNotificationTransport.class);
    DAO.describe(helper, IOSmsNotification.class, IOSmsNotificationTransport.class);

    installNotificationStatus(helper);

  /*  installNotificationStatus(helper);
    installNotificationTransport(helper);
    installNotification(helper);
    installMailNotification(helper);
    installSmsNotification(helper);
    installSmsNotificationTransport(helper);
*/
    return createModuleDocument(helper);
  }

  private ODocument createModuleDocument(OSchemaHelper helper) {
    helper.oClass(Module.CLASS_NAME, OMODULE_CLASS)
            .oProperty(Module.PROP_SEND_PERIOD, OType.LONG)
              .notNull()
              .defaultValue("60000")
            .oProperty(Module.PROP_NOTIFICATIONS_PER_WORKER, OType.INTEGER)
              .notNull()
              .defaultValue("50")
            .oProperty(Module.PROP_SMS_STATUS_URL, OType.STRING);

    return helper.oDocument(OMODULE_NAME, NAME)
            .saveDocument()
            .getODocument();
  }

  private void installNotificationStatus(OSchemaHelper helper) {
    IONotificationStatus status = DAO.create(IONotificationStatus.class);
    IONotificationDAO statusDao = IONotificationDAO.get();

    if (statusDao.getPendingStatus() == null) {
      status.fromStream(new ODocument(IONotificationStatus.CLASS_NAME));
      status.setAlias(IONotificationStatus.ALIAS_PENDING);
      status.setName(CommonUtils.toMap("en", new ResourceModel("notification.status.pending").getObject()));
      status.save();
    }

    if (statusDao.getSendingStatus() == null) {
      status.fromStream(new ODocument(IONotificationStatus.CLASS_NAME));
      status.setAlias(IONotificationStatus.ALIAS_SENDING);
      status.setName(CommonUtils.toMap("en", new ResourceModel("notification.status.sending").getObject()));
      status.save();
    }

    if (statusDao.getSentStatus() == null) {
      status.fromStream(new ODocument(IONotificationStatus.CLASS_NAME));
      status.setAlias(IONotificationStatus.ALIAS_SENT);
      status.setName(CommonUtils.toMap("en", new ResourceModel("notification.status.sent").getObject()));
      status.save();
    }

    if (statusDao.getFailedStatus() == null) {
      status.fromStream(new ODocument(IONotificationStatus.CLASS_NAME));
      status.setAlias(IONotificationStatus.ALIAS_FAILED);
      status.setName(CommonUtils.toMap("en", new ResourceModel("notification.status.failed").getObject()));
      status.save();
    }

  }


/*  private void installNotification(OSchemaHelper helper) {

    helper.oClass(ONotificationStatusHistory.CLASS_NAME)
            .oProperty(ONotificationStatusHistory.PROP_TIMESTAMP, OType.DATETIME, 0)
              .markDisplayable()
              .markAsDocumentName()
            .oProperty(ONotificationStatusHistory.PROP_STATUS, OType.LINK, 10)
              .linkedClass(ONotificationStatus.CLASS_NAME)
              .markDisplayable()
            .oProperty(ONotificationStatusHistory.PROP_NOTIFICATION, OType.LINK, 20)
              .markAsLinkToParent()
              .markDisplayable();

    helper.oAbstractClass(ONotification.CLASS_NAME)
            .oProperty(ONotification.PROP_ID, OType.STRING, 0)
              .notNull()
            .oProperty(ONotification.PROP_STATUS, OType.LINK, 10)
              .notNull()
              .markDisplayable()
              .markAsDocumentName()
              .linkedClass(ONotificationStatus.CLASS_NAME)
            .oProperty(ONotification.PROP_TRANSPORT, OType.LINK, 20)
              .linkedClass(ONotificationTransport.CLASS_NAME)
              .markAsLinkToParent()
              .markDisplayable()
            .oProperty(ONotification.PROP_CREATED, OType.DATETIME, 30)
              .notNull()
              .markDisplayable()
            .oProperty(ONotification.PROP_STATUS_HISTORIES, OType.LINKLIST, 40)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_TABLE);

    helper.setupRelationship(ONotification.CLASS_NAME, ONotification.PROP_STATUS_HISTORIES,
            ONotificationStatusHistory.CLASS_NAME, ONotificationStatusHistory.PROP_NOTIFICATION);
  }

  private void installMailNotification(OSchemaHelper helper) {
    helper.oClass(OMailNotification.CLASS_NAME, ONotification.CLASS_NAME)
            .oProperty(OMailNotification.PROP_PREPARED_MAIL, OType.LINK)
              .linkedClass(OPreparedMail.CLASS_NAME);
  }

  private void installSmsNotification(OSchemaHelper helper) {
    helper.oClass(OSmsNotification.CLASS_NAME, ONotification.CLASS_NAME)
            .oProperty(OSmsNotification.PROP_PREPARED_SMS, OType.LINK)
              .linkedClass(OPreparedSMS.CLASS_NAME);
  }

  private void installSmsNotificationTransport(OSchemaHelper helper) {
    helper.oClass(OSmsNotificationTransport.CLASS_NAME, ONotificationTransport.CLASS_NAME)
            .oProperty(OSmsNotificationTransport.PROP_SMS_SETTINGS, OType.LINK)
              .linkedClass(OSmsSettings.CLASS_NAME);
  }

  private void installNotificationStatus(OSchemaHelper helper) {
    helper.oClass(ONotificationStatus.CLASS_NAME)
            .oProperty(ONotificationStatus.PROP_NAME, OType.EMBEDDEDMAP, 0)
              .linkedType(OType.STRING)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
              .markAsDocumentName()
            .oProperty(ONotificationStatus.PROP_ALIAS, OType.STRING, 10)
              .notNull()
              .oIndex(OClass.INDEX_TYPE.UNIQUE);

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_PENDING)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.pending").getObject()))
            .saveDocument();

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_SENDING)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.sending").getObject()))
            .saveDocument();

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_SENT)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.sent").getObject()))
            .saveDocument();

    helper.oDocument(ONotificationStatus.PROP_ALIAS, ONotificationStatus.ALIAS_FAILED)
            .field(ONotificationStatus.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("notification.status.failed").getObject()))
            .saveDocument();
  }

  private void installNotificationTransport(OSchemaHelper helper) {
    helper.oAbstractClass(ONotificationTransport.CLASS_NAME)
            .oProperty(ONotificationTransport.PROP_NAME, OType.EMBEDDEDMAP)
              .linkedType(OType.STRING)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
              .notNull()
              .markAsDocumentName()
              .markDisplayable()
            .oProperty(ONotificationTransport.PROP_ALIAS, OType.STRING)
              .notNull()
              .oIndex(OClass.INDEX_TYPE.UNIQUE)
              .markAsDocumentName();

    helper.oClass(OMailNotificationTransport.CLASS_NAME, ONotificationTransport.CLASS_NAME)
            .oProperty(OMailNotificationTransport.PROP_MAIL_SETTINGS, OType.LINK)
              .linkedClass(OMailSettings.CLASS_NAME)
              .notNull()
            .oProperty(OMailNotificationTransport.PROP_CONNECTIONS_LIMIT, OType.INTEGER)
              .notNull()
              .defaultValue("1")
              .min("1");
  }*/

  @Override
  public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
    onInstall(app, db);
  }

  @Override
  public void onInitialize(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
    super.onInitialize(app, db, moduleDoc);

    app.getOrientDbSettings().addORecordHooks(ONotificationHook.class);

    long period = new Module(moduleDoc).getSendPeriod();

    ONotificationScheduler.scheduleTask(new ONotificationSendTask(), period);
  }

  @Override
  public void onDestroy(OrienteerWebApplication app, ODatabaseSession db, ODocument moduleDoc) {
    super.onDestroy(app, db, moduleDoc);

    app.getOrientDbSettings().removeORecordHooks(ONotificationHook.class);

    ONotificationScheduler.stopAll();
  }

  /**
   * Model wrapper for {@link ONotificationModule}
   */
  public static class Module extends ODocumentWrapper {

    public static final String CLASS_NAME = "ONotificationModule";

    public static final String PROP_SEND_PERIOD              = "sendPeriod";
    public static final String PROP_NOTIFICATIONS_PER_WORKER = "notificationsPerWorker";
    public static final String PROP_SMS_STATUS_URL           = "smsStatusUrl";

    public Module() {
      this(CLASS_NAME);
    }

    public Module(String iClassName) {
      super(iClassName);
    }

    public Module(ODocument iDocument) {
      super(iDocument);
    }

    public long getSendPeriod() {
      Long period = document.field(PROP_SEND_PERIOD);
      return period != null ? period : 60_000;
    }

    public Module setSendPeriod(long period) {
      document.field(PROP_SEND_PERIOD, period);
      return this;
    }

    public int getNotificationsPerWorker() {
      Integer notifications = document.field(PROP_NOTIFICATIONS_PER_WORKER);
      return notifications != null ? notifications : 50;
    }

    public Module setNotificationsPerWorker(int notifications) {
      document.field(PROP_NOTIFICATIONS_PER_WORKER, notifications);
      return this;
    }

    public String getSmsStatusUrl() {
      return document.field(PROP_SMS_STATUS_URL);
    }

    public Module setSmsStatusUrl(String smsStatusUrl) {
      document.field(PROP_SMS_STATUS_URL, smsStatusUrl);
      return this;
    }
  }
}
