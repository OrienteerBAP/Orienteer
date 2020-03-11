package org.orienteer.notifications.service;

import org.orienteer.notifications.model.ONotification;

import java.io.Closeable;

public interface ITransport<T extends ONotification> extends Closeable {

  void send(T notification);
}
