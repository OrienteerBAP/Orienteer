package org.orienteer.logger.server.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.logger.OLoggerEvent;
import org.orienteer.logger.server.model.OLoggerEventDispatcherModel;
import org.orienteer.logger.server.model.OLoggerEventFilteredDispatcherModel;
import org.orienteer.logger.server.model.OLoggerEventModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository for working with {@link OLoggerEventModel}
 */
public final class OLoggerRepository {

    private OLoggerRepository() {}

    public static OLoggerEventModel storeOLoggerEvent(OLoggerEvent event) {
        return DBClosure.sudo(db -> storeOLoggerEvent(db, event));
    }

    public static OLoggerEventModel storeOLoggerEvent(ODatabaseDocument db, OLoggerEvent event) {
        return storeOLoggerEvent(db, event.toJson());
    }

    public static OLoggerEventModel storeOLoggerEvent(String eventJson) {
        return DBClosure.sudo(db -> storeOLoggerEvent(db, eventJson));
    }

    public static OLoggerEventModel storeOLoggerEvent(ODatabaseDocument db, String eventJson) {
        ODocument doc = new ODocument();
        doc.fromJSON(eventJson);
        Long dateTime = doc.field(OLoggerEventModel.PROP_DATE_TIME, Long.class);
        doc.field(OLoggerEventModel.PROP_DATE_TIME, new Date(dateTime));
        doc.setClassName(OLoggerEventModel.CLASS_NAME);
        doc.save();
        return new OLoggerEventModel(doc);
    }

    public static List<OLoggerEventModel> getEventsByCorrelationId(String correlationId) {
        return DBClosure.sudo(db -> OLoggerRepository.getEventsByCorrelationId(db, correlationId));
    }

    public static List<OLoggerEventModel> getEventsByCorrelationId(ODatabaseDocument db, String correlationId) {
        String sql = String.format("select from %s where %s = ?", OLoggerEventModel.CLASS_NAME,
                OLoggerEventModel.PROP_CORRELATION_ID);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql), correlationId);
        return CommonUtils.mapIdentifiables(identifiables, OLoggerEventModel::new);
    }

    public static Optional<OLoggerEventFilteredDispatcherModel> getOLoggerEventFilteredDispatcher(String alias) {
        return DBClosure.sudo(db -> OLoggerRepository.getOLoggerEventFilteredDispatcher(db, alias));
    }

    public static Optional<OLoggerEventFilteredDispatcherModel> getOLoggerEventFilteredDispatcher(ODatabaseDocument db, String alias) {
        return getOLoggerEventDispatcherAsDocument(db, alias)
                .map(OLoggerEventFilteredDispatcherModel::new);
    }

    public static Optional<OLoggerEventDispatcherModel> getOLoggerEventDispatcher(String alias) {
        return DBClosure.sudo(db -> getOLoggerEventDispatcher(db, alias));
    }

    public static Optional<OLoggerEventDispatcherModel> getOLoggerEventDispatcher(ODatabaseDocument db, String alias) {
        return getOLoggerEventDispatcherAsDocument(db, alias).map(OLoggerEventDispatcherModel::new);
    }

    public static Optional<ODocument> getOLoggerEventDispatcherAsDocument(String alias) {
        return DBClosure.sudo(db -> OLoggerRepository.getOLoggerEventDispatcherAsDocument(db, alias));
    }

    public static Optional<ODocument> getOLoggerEventDispatcherAsDocument(ODatabaseDocument db, String alias) {
        String sql = String.format("select from %s where %s = ?", OLoggerEventDispatcherModel.CLASS_NAME,
                OLoggerEventDispatcherModel.PROP_ALIAS);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), alias);
        return CommonUtils.getDocument(identifiables);
    }
}
