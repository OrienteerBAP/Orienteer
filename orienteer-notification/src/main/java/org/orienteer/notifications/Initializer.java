package org.orienteer.notifications;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.notifications.module.ONotificationModule;

/**
 * {@link IInitializer} for 'orienteer-notifications' module
 */
public class Initializer implements IInitializer {

  @Override
  public void init(Application application) {
    OrienteerWebApplication app = (OrienteerWebApplication)application;
    app.registerModule(ONotificationModule.class);
  }

  @Override
  public void destroy(Application application) {
    OrienteerWebApplication app = (OrienteerWebApplication)application;
    app.unregisterModule(ONotificationModule.class);
  }

}
