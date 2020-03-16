package org.orienteer.notifications.service;

import org.orienteer.notifications.model.ONotification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * Transport pool
 */
public class OTransportPool {

  private final Map<String, ConcurrentLinkedQueue<ITransport<? extends ONotification>>> resources;
  private final Map<String, ConcurrentLinkedQueue<ITransport<? extends ONotification>>> usedResources;

  public OTransportPool() {
    resources = new ConcurrentHashMap<>();
    usedResources = new ConcurrentHashMap<>();
  }

  public synchronized ITransport<? extends ONotification> acquire(String alias, Supplier<ITransport<? extends ONotification>> supplier) {
    ConcurrentLinkedQueue<ITransport<? extends ONotification>> availableTransports = resources.computeIfAbsent(alias,
            k -> new ConcurrentLinkedQueue<>());

    ITransport<? extends ONotification> resource = availableTransports.poll();

    if (resource == null) {
      resource = supplier.get();
    }

    ConcurrentLinkedQueue<ITransport<? extends ONotification>> usedTransports = usedResources.computeIfAbsent(alias, k -> new ConcurrentLinkedQueue<>());
    usedTransports.add(resource);

    return resource;
  }

  public synchronized void release(String alias, ITransport<? extends ONotification> transport) {
    ConcurrentLinkedQueue<ITransport<? extends ONotification>> availableTransports = resources.computeIfAbsent(alias, k -> new ConcurrentLinkedQueue<>());
    ConcurrentLinkedQueue<ITransport<? extends ONotification>> usedTransports = usedResources.computeIfAbsent(alias, k -> new ConcurrentLinkedQueue<>());

    if (usedTransports.contains(transport)) {
      usedTransports.remove(transport);
      availableTransports.add(transport);
    }
  }

}
