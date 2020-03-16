package org.orienteer.notifications.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.notifications.model.ONotification;
import org.orienteer.notifications.model.ONotificationStatus;
import org.orienteer.notifications.service.IONotificationFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for {@link ONotification}
 */
public final class ONotificationRepository {

  private ONotificationRepository() {}

  public static List<ONotification> getNotificationsByStatus(ONotificationStatus status) {
    return DBClosure.sudo(db -> getNotificationsByStatus(db, status));
  }

  public static List<ONotification> getNotificationsByStatus(ODatabaseDocument db, ONotificationStatus status) {
    return getNotificationsByStatus(db, status.getDocument());
  }

  public static List<ONotification> getNotificationsByStatus(ODocument status) {
    return DBClosure.sudo(db -> getNotificationsByStatus(db, status));
  }

  public static List<ONotification> getNotificationsByStatus(ODatabaseDocument db, ODocument status) {
    IONotificationFactory factory = OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationFactory.class);

    return getNotificationsByStatusAsDocuments(db, status)
            .stream()
            .map(factory::create)
            .collect(Collectors.toCollection(LinkedList::new));
  }

  public static List<ODocument> getNotificationsByStatusAsDocuments(ONotificationStatus status) {
    return DBClosure.sudo(db -> getNotificationsByStatusAsDocuments(db, status));
  }

  public static List<ODocument> getNotificationsByStatusAsDocuments(ODatabaseDocument db, ONotificationStatus status) {
    return getNotificationsByStatusAsDocuments(db, status.getDocument());
  }

  public static List<ODocument> getNotificationsByStatusAsDocuments(ODocument status) {
    return DBClosure.sudo(db -> getNotificationsByStatusAsDocuments(db, status));
  }

  public static List<ODocument> getNotificationsByStatusAsDocuments(ODatabaseDocument db, ODocument status) {
    String sql = String.format("select from %s where %s = ?", ONotification.CLASS_NAME, ONotification.PROP_STATUS);
    List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql), status);
    return CommonUtils.getDocuments(identifiables);
  }

  public static List<ONotification> getPendingNotifications(ODatabaseDocument db) {
    ONotificationStatus status = ONotificationStatusRepository.getPendingStatus(db);
    return getNotificationsByStatus(db, status);
  }
}
