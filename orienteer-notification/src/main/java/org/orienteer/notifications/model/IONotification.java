package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.transponder.annotation.EntityProperty;
import org.orienteer.transponder.annotation.EntityType;
import org.orienteer.transponder.orientdb.IODocumentWrapper;
import org.orienteer.transponder.orientdb.OrientDBProperty;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper class for {@link IONotification#CLASS_NAME}
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IONotification.CLASS_NAME, isAbstract = true)
public interface IONotification extends IODocumentWrapper {

  String CLASS_NAME = "ONotification";

  String getId();
  IONotification setId(String id);

  @OrientDBProperty(notNull = true)
  Date getCreated();
  IONotification setCreated(Date created);

  @EntityProperty(referencedType = IONotificationStatus.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK, notNull = true)
  ODocument getStatus();
  IONotification setStatus(ODocument status);

  @EntityProperty(referencedType = IONotificationStatusHistory.CLASS_NAME, inverse = "notification")
  @OrientDBProperty(type = OType.LINKLIST)
  List<ODocument> getStatusHistories();
  IONotification setStatusHistories(List<ODocument> histories);

  @EntityProperty(referencedType = IONotificationTransport.CLASS_NAME)
  @OrientDBProperty(type = OType.LINK, notNull = true)
  ODocument getTransport();
  IONotification setTransport(ODocument transport);

  default IONotification addStatusHistory(ODocument history) {
    List<ODocument> histories = getStatusHistories();
    if (histories == null) {
      histories = new LinkedList<>();
    }/* else {
      histories = new LinkedList<>(histories);
    }*/
    histories.add(history);
    return setStatusHistories(histories);
  }

  default IONotification addStatusHistory(IONotificationStatusHistory history) {
    return addStatusHistory(history.getDocument());
  }

}
