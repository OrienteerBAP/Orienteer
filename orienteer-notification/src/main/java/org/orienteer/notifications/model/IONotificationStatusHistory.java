package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.*;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.IODocumentWrapper;
import org.orienteer.transponder.orientdb.OrientDBProperty;

import java.util.Date;

/**
 * Wrapper class for {@link IONotificationStatusHistory#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IONotificationStatusHistory.CLASS_NAME)
public interface IONotificationStatusHistory extends IODocumentWrapper {

  String CLASS_NAME = "ONotificationStatusHistory";

  Date getTimestamp();
  IONotificationStatusHistory setTimestamp(Date timestamp);

  @EntityProperty(referencedType = IONotification.CLASS_NAME, inverse = "statusHistories")
  @OrientDBProperty(type = OType.LINK)
  ODocument getNotification();
  IONotificationStatusHistory setNotification(ODocument notification);

  @EntityProperty(referencedType = IONotificationStatus.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK)
  ODocument getStatus();
  IONotificationStatusHistory setStatus(ODocument status);

  static IONotificationStatusHistory create(Date timestamp, IONotificationStatus status) {
    return create(timestamp, status.getDocument());
  }

  static IONotificationStatusHistory create(Date timestamp, ODocument status) {
    IONotificationStatusHistory statusHistory = DAO.create(IONotificationStatusHistory.class);
    statusHistory.fromStream(new ODocument(CLASS_NAME));
    statusHistory.setStatus(status);
    statusHistory.setTimestamp(timestamp);
    return statusHistory;
  }

}
