package org.orienteer.notifications.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;

public class ONotificationsModule extends AbstractOrienteerModule {

  public static final String NAME = "orienteer-notifications";
  public static final int VERSION = 1;

  protected ONotificationsModule() {
    super(NAME, VERSION);
  }

  @Override
  public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
    return null;
  }

  @Override
  public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
    onInstall(app, db);
  }
}
