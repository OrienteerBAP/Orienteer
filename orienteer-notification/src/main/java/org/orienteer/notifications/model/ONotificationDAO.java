package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;

import java.util.List;

/**
 * Provide access for class {@link org.orienteer.notifications.model.ONotification#CLASS_NAME}
 */
@ProvidedBy(DAOProvider.class)
public interface ONotificationDAO {

  static ONotificationDAO get() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(ONotificationDAO.class);
  }

  @Query("select from ONotification where status = :status")
  List<ODocument> findNotificationsByStatus(ODocument status);

  @Query("select from ONotification where status != status")
  List<ODocument> findNotificationsExceptStatus(ODocument status);


  @Query("select from ONotificationStatus where alias = :alias")
  ODocument findStatusByAlias(String alias);

  @Query("select from ONotificationTransport where alias = :alias")
  ODocument findTransportByAlias(String alias);

  default ODocument getPendingStatus() {
    return findStatusByAlias(ONotificationStatus.ALIAS_PENDING);
  }

  default ODocument getSendingStatus() {
    return findStatusByAlias(ONotificationStatus.ALIAS_SENDING);
  }

  default ODocument getSentStatus() {
    return findStatusByAlias(ONotificationStatus.ALIAS_SENT);
  }

  default ODocument getFailedStatus() {
    return findStatusByAlias(ONotificationStatus.ALIAS_FAILED);
  }
}
