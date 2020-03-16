package org.orienteer.notifications.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.notifications.model.ONotificationStatus;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link ONotificationStatus}
 */
public final class ONotificationStatusRepository {

  private ONotificationStatusRepository() {}

  public static Optional<ONotificationStatus> getStatusByAlias(ODatabaseDocument db, String alias) {
    return getStatusByAliasAsDocument(db, alias).map(ONotificationStatus::new);
  }

  public static Optional<ODocument> getStatusByAliasAsDocument(ODatabaseDocument db, String alias) {
    String sql = String.format("select from %s where %s = ?", ONotificationStatus.CLASS_NAME, ONotificationStatus.PROP_ALIAS);
    List<OIdentifiable> statuses = db.query(new OSQLSynchQuery<>(sql, 1), alias);
    return CommonUtils.getDocument(statuses);
  }

  public static ONotificationStatus getPendingStatus() {
    return DBClosure.sudo(ONotificationStatusRepository::getPendingStatus);
  }

  public static ONotificationStatus getPendingStatus(ODatabaseDocument db) {
    return ONotificationStatusRepository.getStatusByAlias(db, ONotificationStatus.ALIAS_PENDING)
            .orElseThrow(() -> new IllegalStateException("There is no " + ONotificationStatus.CLASS_NAME + "." + ONotificationStatus.ALIAS_PENDING));
  }

  public static ONotificationStatus getSendingStatus() {
    return DBClosure.sudo(ONotificationStatusRepository::getSendingStatus);
  }

  public static ONotificationStatus getSendingStatus(ODatabaseDocument db) {
    return ONotificationStatusRepository.getStatusByAlias(db, ONotificationStatus.ALIAS_SENDING)
            .orElseThrow(() -> new IllegalStateException("There is no " + ONotificationStatus.CLASS_NAME + "." + ONotificationStatus.ALIAS_SENDING));
  }

  public static ONotificationStatus getSentStatus() {
    return DBClosure.sudo(ONotificationStatusRepository::getSentStatus);
  }

  public static ONotificationStatus getSentStatus(ODatabaseDocument db) {
    return ONotificationStatusRepository.getStatusByAlias(db, ONotificationStatus.ALIAS_SENT)
            .orElseThrow(() -> new IllegalStateException("There is no " + ONotificationStatus.CLASS_NAME + "." + ONotificationStatus.ALIAS_SENT));
  }

  public static ONotificationStatus getFailedStatus() {
    return DBClosure.sudo(ONotificationStatusRepository::getFailedStatus);
  }

  public static ONotificationStatus getFailedStatus(ODatabaseDocument db) {
    return ONotificationStatusRepository.getStatusByAlias(db, ONotificationStatus.ALIAS_FAILED)
            .orElseThrow(() -> new IllegalStateException("There is no " + ONotificationStatus.CLASS_NAME + "." + ONotificationStatus.ALIAS_FAILED));
  }
}
