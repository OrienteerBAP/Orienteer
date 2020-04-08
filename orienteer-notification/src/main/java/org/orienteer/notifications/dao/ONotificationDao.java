package org.orienteer.notifications.dao;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;

import java.util.List;

@ProvidedBy(DAOProvider.class)
public interface ONotificationDao {

  static ONotificationDao get() {
    return OrienteerWebApplication.lookupApplication().getServiceInstance(ONotificationDao.class);
  }

  @Query("select from ONotification where status = :status")
  List<ODocument> findByStatus(ODocument status);

  @Query("select from ONotification where status != status")
  List<ODocument> findExceptStatus(ODocument status);


}
