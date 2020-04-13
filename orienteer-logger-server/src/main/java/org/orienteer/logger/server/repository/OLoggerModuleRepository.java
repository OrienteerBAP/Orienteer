package org.orienteer.logger.server.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.logger.server.OLoggerModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;

/**
 * Repository for working with logger module
 */
public final class OLoggerModuleRepository {

    private OLoggerModuleRepository() {}


    public static OLoggerModule.Module getModule() {
        return DBClosure.sudo(OLoggerModuleRepository::getModule);
    }

    public static OLoggerModule.Module getModule(ODatabaseDocument db) {
        String sql = String.format("select from %s where %s = ?", OLoggerModule.Module.CLASS_NAME, OLoggerModule.OMODULE_NAME);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), OLoggerModule.NAME);
        return CommonUtils.getDocument(identifiables)
                .map(OLoggerModule.Module::new)
                .orElseThrow(() -> new IllegalStateException("There is no module with name: " + OLoggerModule.NAME + " in database"));
    }
}
