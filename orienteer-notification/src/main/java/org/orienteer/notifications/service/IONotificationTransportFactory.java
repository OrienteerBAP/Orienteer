package org.orienteer.notifications.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.IONotificationTransport;

/**
 * Factory for {@link IONotificationTransport}
 */
@ImplementedBy(ONotificationTransportFactory.class)
public interface IONotificationTransportFactory {

  IONotificationTransport create(ODocument document);

}
