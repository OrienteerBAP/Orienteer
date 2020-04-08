package org.orienteer.notifications.service;

import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.Closeable;

/**
 * Transport for given notification
 * @param <T> notification type for send
 */
public interface ITransport extends Closeable {

  void send(ODocument notification);
}
