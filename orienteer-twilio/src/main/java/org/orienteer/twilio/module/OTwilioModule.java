package org.orienteer.twilio.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.twilio.hook.OPreparedSMSHook;
import org.orienteer.twilio.model.OPreparedSMS;
import org.orienteer.twilio.model.OSMS;
import org.orienteer.twilio.model.OSmsSettings;

/**
 * Module for install data model for 'orienteer-notifications'
 */
public class OTwilioModule extends AbstractOrienteerModule {

  public static final String NAME = "orienteer-twilio";
  public static final int VERSION = 2;

  protected OTwilioModule() {
    super(NAME, VERSION);
  }

  @Override
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
    OSchemaHelper helper = OSchemaHelper.bind(db);
    installSmsSettings(helper);
    installSms(helper);
    installPreparedSms(helper);
    return super.onInstall(app, db);
  }

  private void installSmsSettings(OSchemaHelper helper) {
    helper.oClass(OSmsSettings.CLASS_NAME)
            .oProperty(OSmsSettings.PROP_NAME, OType.STRING, 0)
              .notNull()
              .markDisplayable()
              .markAsDocumentName()
            .oProperty(OSmsSettings.PROP_TWILIO_PHONE_NUMBER, OType.STRING, 10)
              .markDisplayable()
              .notNull()
            .oProperty(OSmsSettings.PROP_TWILIO_ACCOUNT_SID, OType.STRING, 20)
              .notNull()
            .oProperty(OSmsSettings.PROP_TWILIO_AUTH_TOKEN, OType.STRING, 30)
              .notNull()
            .oProperty(OSmsSettings.PROP_ALIAS, OType.STRING, 40)
              .notNull()
              .markDisplayable();
  }

  private void installSms(OSchemaHelper helper) {
    helper.oClass(OSMS.CLASS_NAME)
            .oProperty(OSMS.PROP_NAME, OType.STRING, 0)
              .markDisplayable()
              .markAsDocumentName()
              .notNull()
            .oProperty(OSMS.PROP_TEXT, OType.STRING, 10)
              .assignVisualization(UIVisualizersRegistry.VISUALIZER_TEXTAREA)
              .notNull()
              .markDisplayable()
            .oProperty(OSMS.PROP_SETTINGS, OType.LINK, 20)
              .notNull()
              .linkedClass(OSmsSettings.CLASS_NAME)
              .markDisplayable();
  }

  private void installPreparedSms(OSchemaHelper helper) {
    helper.oClass(OPreparedSMS.CLASS_NAME)
            .oProperty(OPreparedSMS.PROP_ID, OType.STRING, 0)
              .markAsDocumentName()
              .notNull()
              .oIndex(OClass.INDEX_TYPE.UNIQUE)
            .oProperty(OPreparedSMS.PROP_SMS, OType.LINK, 10)
              .linkedClass(OSMS.CLASS_NAME)
              .notNull()
              .markAsLinkToParent()
            .oProperty(OPreparedSMS.PROP_TEXT, OType.STRING, 20)
              .notNull()
            .oProperty(OPreparedSMS.PROP_ATTACHMENTS, OType.EMBEDDEDLIST, 30)
              .linkedType(OType.STRING)
            .oProperty(OPreparedSMS.PROP_RECIPIENT, OType.STRING, 40)
              .notNull()
            .oProperty(OPreparedSMS.PROP_SUCCESS, OType.BOOLEAN, 50)
              .defaultValue("false")
              .notNull()
            .oProperty(OPreparedSMS.PROP_TIMESTAMP, OType.DATETIME, 60)
              .notNull()
              .updateCustomAttribute(CustomAttribute.UI_READONLY, true);
  }

  @Override
  public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
    super.onInitialize(app, db);
    app.getOrientDbSettings().getORecordHooks().add(OPreparedSMSHook.class);
    app.mountPackage("org.orienteer.twilio.resource");
  }

  @Override
  public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
    super.onDestroy(app, db);
    app.getOrientDbSettings().getORecordHooks().remove(OPreparedSMSHook.class);

    app.unmountPackage("org.orienteer.twilio.resource");
  }

  @Override
  public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
    super.onUpdate(app, db, oldVersion, newVersion);

    onInstall(app, db);
  }
}
