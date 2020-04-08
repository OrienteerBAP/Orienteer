package org.orienteer.notifications.testenv.service;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.service.ITransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OTestTransport implements ITransport {

  public static final Logger LOG = LoggerFactory.getLogger(OTestTransport.class);

  public OTestTransport(ODocument transportDocument) {
    super();
    LOG.info("Open transport connection for: {}", transportDocument);
  }

  @Override
  public void send(ODocument notification) {
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
