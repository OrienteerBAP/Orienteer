package org.orienteer.notifications.service;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.notifications.model.ONotification;

/**
 * Factory for {@link ONotification}
 */
@ImplementedBy(ONotificationFactory.class)
public interface IONotificationFactory {

  ONotification create(ODocument document);

}
