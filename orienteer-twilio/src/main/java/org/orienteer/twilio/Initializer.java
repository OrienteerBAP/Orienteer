package org.orienteer.twilio;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.twilio.module.OTwilioModule;

/**
 * {@link IInitializer} for 'orienteer-twilio' module
 */
public class Initializer implements IInitializer {

  @Override
  public void init(Application application) {
    OrienteerWebApplication app = (OrienteerWebApplication)application;
    app.registerModule(OTwilioModule.class);
  }

  @Override
  public void destroy(Application application) {
    OrienteerWebApplication app = (OrienteerWebApplication)application;
    app.unregisterModule(OTwilioModule.class);
  }

}
