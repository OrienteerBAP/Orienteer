package org.orienteer.notifications.testenv.module;

import com.google.inject.AbstractModule;
import org.orienteer.core.service.OverrideModule;
import org.orienteer.notifications.service.IONotificationFactory;
import org.orienteer.notifications.service.IONotificationTransportFactory;
import org.orienteer.notifications.testenv.service.ONotificationTestFactory;
import org.orienteer.notifications.testenv.service.ONotificationTestTransportFactory;

@OverrideModule
public class TestInitModule extends AbstractModule {

  @Override
  protected void configure() {
    super.configure();

    bind(IONotificationTransportFactory.class).to(ONotificationTestTransportFactory.class);
    bind(IONotificationFactory.class).to(ONotificationTestFactory.class);
  }
}
