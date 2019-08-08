package org.orienteer.logger.server.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.OLoggerModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Repository for working with logger module
 */
public final class OLoggerModuleRepository {

    private OLoggerModuleRepository() {}


    public static OLoggerModule.Module getModule() {
        return DBClosure.sudo(OLoggerModuleRepository::getModule);
    }

    public static OLoggerModule.Module getModule(ODatabaseDocument db) {
        String sql = String.format("select from %s where %s = ? limit 1", OLoggerModule.Module.CLASS_NAME, OLoggerModule.OMODULE_NAME);

        return db.query(sql)
                .elementStream()
                .map(e -> new OLoggerModule.Module((ODocument) e))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no module with name: " + OLoggerModule.NAME + " in database"));
    }
}
