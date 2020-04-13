package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.*;

import java.util.Date;

/**
 * Wrapper class for {@link IONotificationStatusHistory#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IONotificationStatusHistory.CLASS_NAME)
public interface IONotificationStatusHistory extends IODocumentWrapper {

  String CLASS_NAME = "ONotificationStatusHistory";

  Date getTimestamp();
  IONotificationStatusHistory setTimestamp(Date timestamp);

  @DAOField(linkedClass = IONotification.CLASS_NAME, inverse = "statusHistories", type = OType.LINK)
  ODocument getNotification();
  IONotificationStatusHistory setNotification(ODocument notification);

  @DAOField(linkedClass = IONotificationStatus.CLASS_NAME, type = OType.LINK)
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
