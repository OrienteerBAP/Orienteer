package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.ODocumentWrapperProvider;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper class for {@link IONotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IONotification.CLASS_NAME, isAbstract = true)
public interface IONotification extends IODocumentWrapper {

  String CLASS_NAME = "ONotification";

  String getId();
  IONotification setId(String id);

  @DAOField(notNull = true)
  Date getCreated();
  IONotification setCreated(Date created);

  @DAOField(linkedClass = IONotificationStatus.CLASS_NAME, type = OType.LINK, notNull = true)
  ODocument getStatus();
  IONotification setStatus(ODocument status);

  @DAOField(linkedClass = IONotificationStatusHistory.CLASS_NAME, type = OType.LINKLIST, inverse = "notification")
  List<ODocument> getStatusHistories();
  IONotification setStatusHistories(List<ODocument> histories);

  @DAOField(linkedClass = IONotificationTransport.CLASS_NAME, type = OType.LINK, notNull = true)
  ODocument getTransport();
  IONotification setTransport(ODocument transport);

  default IONotification addStatusHistory(ODocument history) {
    List<ODocument> histories = getStatusHistories();
    if (histories == null) {
      histories = new LinkedList<>();
    } else {
      histories = new LinkedList<>(histories);
    }
    histories.add(history);
    return setStatusHistories(histories);
  }

  default IONotification addStatusHistory(IONotificationStatusHistory history) {
    return addStatusHistory(history.getDocument());
  }

}
