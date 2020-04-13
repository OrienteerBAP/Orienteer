package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;

import java.util.List;

/**
 * Provide access for class {@link org.orienteer.notifications.model.IONotification#CLASS_NAME}
 */
@ProvidedBy(DAOProvider.class)
public interface IONotificationDAO {

  static IONotificationDAO get() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationDAO.class);
  }

  @Query("select from ONotification where status = :status")
  List<ODocument> findNotificationsByStatus(ODocument status);

  @Query("select from ONotification where status != :status")
  List<ODocument> findNotificationsExceptStatus(ODocument status);


  @Query("select from ONotificationStatus where alias = :alias")
  ODocument findStatusByAlias(String alias);

  @Query("select from ONotificationTransport where alias = :alias")
  ODocument findTransportByAlias(String alias);

  default ODocument getPendingStatus() {
    return findStatusByAlias(IONotificationStatus.ALIAS_PENDING);
  }

  default ODocument getSendingStatus() {
    return findStatusByAlias(IONotificationStatus.ALIAS_SENDING);
  }

  default ODocument getSentStatus() {
    return findStatusByAlias(IONotificationStatus.ALIAS_SENT);
  }

  default ODocument getFailedStatus() {
    return findStatusByAlias(IONotificationStatus.ALIAS_FAILED);
  }
}
