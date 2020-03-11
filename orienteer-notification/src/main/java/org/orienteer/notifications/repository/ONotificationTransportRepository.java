package org.orienteer.notifications.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.notifications.model.ONotificationTransport;
import org.orienteer.notifications.service.IONotificationTransportFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

public final class ONotificationTransportRepository {

  private ONotificationTransportRepository() {}

  public static Optional<ONotificationTransport> getTransportByAlias(String alias) {
    return DBClosure.sudo(db -> getTransportByAlias(db, alias));
  }

  public static Optional<ODocument> getTransportByAliasAsDocument(String alias) {
    return DBClosure.sudo(db -> getTransportByAliasAsDocument(db, alias));
  }

  public static Optional<ONotificationTransport> getTransportByAlias(ODatabaseDocument db, String alias) {
    return getTransportByAliasAsDocument(db, alias).map(doc -> {
      IONotificationTransportFactory factory = OrienteerWebApplication.lookupApplication().getServiceInstance(IONotificationTransportFactory.class);
      return factory.create(doc);
    });
  }

  public static Optional<ODocument> getTransportByAliasAsDocument(ODatabaseDocument db, String alias) {
    String sql = String.format("select from %s where %s = ?", ONotificationTransport.CLASS_NAME, ONotificationTransport.PROP_ALIAS);
    List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), alias);
    return CommonUtils.getDocument(identifiables);
  }
}
