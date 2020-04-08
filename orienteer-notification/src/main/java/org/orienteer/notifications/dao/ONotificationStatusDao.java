package org.orienteer.notifications.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;
import org.orienteer.notifications.model.ONotificationStatus;

/**
 * Provide access for class {@link org.orienteer.notifications.model.ONotificationStatus#CLASS_NAME}
 */
@ProvidedBy(DAOProvider.class)
public interface ONotificationStatusDao {

  static ONotificationStatusDao get() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(ONotificationStatusDao.class);
  }

  @Query("select from ONotificationStatus where alias = :alias")
  ODocument findByAlias(String alias);


  default ODocument getPending() {
    return findByAlias(ONotificationStatus.ALIAS_PENDING);
  }

  default ODocument getSending() {
    return findByAlias(ONotificationStatus.ALIAS_SENDING);
  }

  default ODocument getSent() {
    return findByAlias(ONotificationStatus.ALIAS_SENT);
  }

  default ODocument getFailed() {
    return findByAlias(ONotificationStatus.ALIAS_FAILED);
  }

}
