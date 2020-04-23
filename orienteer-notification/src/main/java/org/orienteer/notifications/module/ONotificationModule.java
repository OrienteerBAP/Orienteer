package org.orienteer.notifications.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
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
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
    OSchemaHelper helper = OSchemaHelper.bind(db);

    DAO.describe(helper, IONotification.class, IONotificationStatus.class, IONotificationTransport.class, IONotificationStatusHistory.class);
    DAO.describe(helper, IOMailNotification.class, IOMailNotificationTransport.class);
    DAO.describe(helper, IOSmsNotification.class, IOSmsNotificationTransport.class);

    installNotificationStatus(helper);

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


  @Override
  public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
    onInstall(app, db);
  }

  @Override
  public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
    super.onInitialize(app, db, moduleDoc);

    List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
    hooks.add(ONotificationHook.class);

    long period = new Module(moduleDoc).getSendPeriod();

    ONotificationScheduler.scheduleTask(new ONotificationSendTask(), period);
  }

  @Override
  public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db, ODocument moduleDoc) {
    super.onDestroy(app, db, moduleDoc);

    List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
    hooks.remove(ONotificationHook.class);

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
