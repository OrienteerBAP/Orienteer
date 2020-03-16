package org.orienteer.notifications.service;

import org.orienteer.notifications.model.ONotification;

import java.io.Closeable;

/**
 * Transport for given notification
 * @param <T> notification type for send
 */
public interface ITransport<T extends ONotification> extends Closeable {

  void send(T notification);
}
