package org.orienteer.notifications.testenv;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.notifications.testenv.module.TestDataModule;

public class TestEnvInitializer implements IInitializer {

  @Override
  public void init(Application application) {
    OrienteerWebApplication app = (OrienteerWebApplication)application;
    app.registerModule(TestDataModule.class);
  }

  @Override
  public void destroy(Application application) {
    OrienteerWebApplication app = (OrienteerWebApplication)application;
    app.unregisterModule(TestDataModule.class);
  }
}
