package org.orienteer.notifications.testenv.service;

import org.orienteer.notifications.service.ITransport;
import org.orienteer.notifications.testenv.OTestNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OTestTransport implements ITransport<OTestNotification> {

  public static final Logger LOG = LoggerFactory.getLogger(OTestTransport.class);

  public OTestTransport() {
    super();
    LOG.info("Open transport connection for: {}", getClass().getSimpleName());
  }

  @Override
  public void send(OTestNotification notification) {
    try {
      Thread.sleep(3000);
      LOG.info("Send notification: {}", notification);
    } catch (InterruptedException e) {}
  }

  @Override
  public void close() throws IOException {
    LOG.info("Close transport connection for: {}", getClass().getSimpleName());
  }
}
