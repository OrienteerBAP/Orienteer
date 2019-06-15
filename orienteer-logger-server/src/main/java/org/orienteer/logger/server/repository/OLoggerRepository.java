package org.orienteer.logger.server.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.logger.server.OLoggerModule;
import org.orienteer.logger.server.model.OLoggerEventModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Date;

/**
 * Repository for working with {@link OLoggerEventModel}
 */
public final class OLoggerRepository {

    private OLoggerRepository() {}

    public static OLoggerEventModel storeOLoggerEvent(String event) {
        return DBClosure.sudo(db -> storeOLoggerEvent(db, event));
    }

    public static OLoggerEventModel storeOLoggerEvent(ODatabaseDocument db, String event) {
        ODocument doc = new ODocument();
        doc.fromJSON(event);
        Long dateTime = doc.field(OLoggerEventModel.PROP_DATE_TIME, Long.class);
        doc.field(OLoggerEventModel.PROP_DATE_TIME, new Date(dateTime));
        doc.setClassName(OLoggerEventModel.CLASS_NAME);
        doc.save();
        return new OLoggerEventModel(doc);
    }
}
