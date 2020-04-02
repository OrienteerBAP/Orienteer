package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import java.util.Date;

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = ONotificationStatusHistory.CLASS_NAME)
public interface ONotificationStatusHistory extends IODocumentWrapper {

  String CLASS_NAME = "ONotificationStatusHistory";

  Date getTimestamp();
  ONotificationStatusHistory setTimestamp(Date timestamp);

  @DAOField(linkedClass = ONotification.CLASS_NAME, inverse = "statusHistories", type = OType.LINK)
  ODocument getNotification();

  @DAOField(linkedClass = ONotification.CLASS_NAME, inverse = "statusHistories", type = OType.LINK)
  ONotificationStatusHistory setNotification(ODocument notification);

  @DAOField(linkedClass = ONotificationStatus.CLASS_NAME, type = OType.LINK)
  ODocument getStatus();

  @DAOField(linkedClass = ONotificationStatus.CLASS_NAME, type = OType.LINK)
  ONotificationStatusHistory setStatus(ODocument status);

  static ONotificationStatusHistory create(Date timestamp, ONotificationStatus status) {
    return create(timestamp, status.getDocument());
  }

  static ONotificationStatusHistory create(Date timestamp, ODocument status) {
    ONotificationStatusHistory statusHistory = IODocumentWrapper.get(ONotificationStatusHistory.class);
    statusHistory.fromStream(new ODocument(CLASS_NAME));
    statusHistory.setStatus(status);
    statusHistory.setTimestamp(timestamp);
    return statusHistory;
  }

}
