package org.orienteer.notifications.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * Transport pool
 */
public class OTransportPool {

  private final Map<String, ConcurrentLinkedQueue<ITransport>> resources;
  private final Map<String, ConcurrentLinkedQueue<ITransport>> usedResources;

  public OTransportPool() {
    resources = new ConcurrentHashMap<>();
    usedResources = new ConcurrentHashMap<>();
  }

  public synchronized ITransport acquire(String alias, Supplier<ITransport> supplier) {
    ConcurrentLinkedQueue<ITransport> availableTransports = resources.computeIfAbsent(alias,
            k -> new ConcurrentLinkedQueue<>());

    ITransport resource = availableTransports.poll();

    if (resource == null) {
      resource = supplier.get();
    }

    ConcurrentLinkedQueue<ITransport> usedTransports = usedResources.computeIfAbsent(alias, k -> new ConcurrentLinkedQueue<>());
    usedTransports.add(resource);

    return resource;
  }

  public synchronized void release(String alias, ITransport transport) {
    ConcurrentLinkedQueue<ITransport> availableTransports = resources.computeIfAbsent(alias, k -> new ConcurrentLinkedQueue<>());
    ConcurrentLinkedQueue<ITransport> usedTransports = usedResources.computeIfAbsent(alias, k -> new ConcurrentLinkedQueue<>());

    if (usedTransports.contains(transport)) {
      usedTransports.remove(transport);
      availableTransports.add(transport);
    }
  }

}
