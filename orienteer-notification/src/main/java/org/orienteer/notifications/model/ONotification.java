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

@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = ONotification.CLASS_NAME, isAbstract = true)
public interface ONotification extends IODocumentWrapper {

  String CLASS_NAME = "ONotification";

  String getId();
  ONotification setId(String id);

  Date getCreated();
  ONotification setCreated(Date created);

  @DAOField(linkedClass = ONotificationStatus.CLASS_NAME, type = OType.LINK)
  ODocument getStatus();

  @DAOField(linkedClass = ONotificationStatus.CLASS_NAME, type = OType.LINK)
  ONotification setStatus(ODocument status);

  @DAOField(linkedClass = ONotificationStatusHistory.CLASS_NAME, type = OType.LINKLIST, inverse = "notification")
  List<ODocument> getStatusHistories();

  @DAOField(linkedClass = ONotificationStatusHistory.CLASS_NAME, type = OType.LINKLIST, inverse = "notification")
  ONotification setStatusHistories(List<ODocument> histories);

  @DAOField(linkedClass = ONotificationTransport.CLASS_NAME, type = OType.LINK)
  ODocument getTransport();

  @DAOField(linkedClass = ONotificationTransport.CLASS_NAME, type = OType.LINK)
  ONotification setTransport(ODocument transport);

  default ONotification addStatusHistory(ODocument history) {
    List<ODocument> histories = getStatusHistories();
    if (histories == null) {
      histories = new LinkedList<>();
    } else {
      histories = new LinkedList<>(histories);
    }
    histories.add(history);
    return setStatusHistories(histories);
  }

  default ONotification addStatusHistory(ONotificationStatusHistory history) {
    return addStatusHistory(history.getDocument());
  }

}
