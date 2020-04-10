package org.orienteer.notifications.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.IONotification;

/**
 * Factory for {@link IONotification}
 */
@ImplementedBy(ONotificationFactory.class)
public interface IONotificationFactory {

  IONotification create(ODocument document);

}
