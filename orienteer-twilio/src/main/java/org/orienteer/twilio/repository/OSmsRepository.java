package org.orienteer.twilio.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.twilio.model.OSMS;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

/**
 * Repository for work with {@link OSMS}
 */
public final class OSmsRepository {

  private OSmsRepository() {}

  public static Optional<OSMS> getSmsByName(String name) {
    return DBClosure.sudo(db -> getSmsByName(db, name));
  }

  public static Optional<OSMS> getSmsByName(ODatabaseDocument db, String name) {
    return getSmsByNameAsDocument(db, name).map(OSMS::new);
  }

  public static Optional<ODocument> getSmsByNameAsDocument(String name) {
    return DBClosure.sudo(db -> getSmsByNameAsDocument(db, name));
  }

  public static Optional<ODocument> getSmsByNameAsDocument(ODatabaseDocument db, String name) {
    String sql = String.format("select from %s where %s = ?", OSMS.CLASS_NAME, OSMS.PROP_NAME);
    List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
    return CommonUtils.getDocument(identifiables);
  }
}
