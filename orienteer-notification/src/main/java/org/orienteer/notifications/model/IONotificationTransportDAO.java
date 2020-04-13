package org.orienteer.notifications.model;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.dao.DAOProvider;
import org.orienteer.core.dao.Query;

/**
 * Provide access for class {@link org.orienteer.notifications.model.IONotificationTransport#CLASS_NAME}
 */
@ProvidedBy(DAOProvider.class)
public interface IONotificationTransportDAO {

  @Query("select from ONotificationTransport where alias = :alias")
  ODocument getTransportByAlias(String alias);

}
