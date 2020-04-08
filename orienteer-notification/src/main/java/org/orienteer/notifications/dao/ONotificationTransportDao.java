package org.orienteer.notifications.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;

/**
 * Provide access for class {@link org.orienteer.notifications.model.ONotificationTransport#CLASS_NAME}
 */
@ProvidedBy(DAOProvider.class)
public interface ONotificationTransportDao {

  static ONotificationTransportDao get() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(ONotificationTransportDao.class);
  }

  @Query("select from ONotificationTransport where alias = :alias")
  ODocument findByAlias(String alias);

}
