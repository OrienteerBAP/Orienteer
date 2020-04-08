package org.orienteer.notifications.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.ONotificationTransport;

/**
 * Factory for {@link ONotificationTransport}
 */
@ImplementedBy(ONotificationTransportFactory.class)
public interface IONotificationTransportFactory {

  ONotificationTransport create(ODocument document);

}
