package org.orienteer.twilio.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.twilio.model.OSmsSettings;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

/**
 * Repository for work with {@link org.orienteer.twilio.model.OSmsSettings}
 */
public class OSmsSettingsRepository {

  private OSmsSettingsRepository() {}

  public static Optional<OSmsSettings> getSettingsByAlias(String alias) {
    return DBClosure.sudo(db -> getSettingsByAlias(db, alias));
  }

  public static Optional<ODocument> getSettingsByAliasAsDocument(String alias) {
    return DBClosure.sudo(db -> getSettingsByAliasAsDocument(db, alias));
  }

  public static Optional<OSmsSettings> getSettingsByAlias(ODatabaseDocument db, String alias) {
    return getSettingsByAliasAsDocument(db, alias).map(OSmsSettings::new);
  }

  public static Optional<ODocument> getSettingsByAliasAsDocument(ODatabaseDocument db, String alias) {
    String sql = String.format("select from %s where %s = ?", OSmsSettings.CLASS_NAME, OSmsSettings.PROP_ALIAS);
    List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), alias);
    return CommonUtils.getDocument(identifiables);
  }

}
