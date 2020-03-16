package org.orienteer.notifications.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.notifications.module.ONotificationModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;

/**
 * Repository for {@link ONotificationModule.Module}
 */
public final class ONotificationModuleRepository {

  private ONotificationModuleRepository() {}

  public static ONotificationModule.Module getModule() {
    return DBClosure.sudo(ONotificationModuleRepository::getModule);
  }

  public static ONotificationModule.Module getModule(ODatabaseDocument db) {
    return new ONotificationModule.Module(getModuleAsDocument(db));
  }

  public static ODocument getModuleAsDocument() {
    return DBClosure.sudo(ONotificationModuleRepository::getModuleAsDocument);
  }

  public static ODocument getModuleAsDocument(ODatabaseDocument db) {
    String sql = String.format("select from %s where %s = ?", ONotificationModule.OMODULE_CLASS, ONotificationModule.OMODULE_NAME);
    List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), ONotificationModule.NAME);
    return CommonUtils.getDocument(identifiables)
            .orElseThrow(() -> new IllegalStateException("There is no module with name: " + ONotificationModule.NAME));
  }

}
