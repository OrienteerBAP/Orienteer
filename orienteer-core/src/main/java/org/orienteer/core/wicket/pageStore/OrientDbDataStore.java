package org.orienteer.core.wicket.pageStore;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.pageStore.IDataStore;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Default implementation of {@link IDataStore} which stores data in OrientDB
 */
public class OrientDbDataStore implements IDataStore {

    @Override
    public byte[] getData(String sessionId, int id) {
        return getWicketData(sessionId, id)
                .map(OWicketData::getData)
                .orElse(null);
    }

    @Override
    public void removeData(String sessionId, int id) {
        DBClosure.sudoConsumer(db -> {
            String sql = String.format("delete from %s where %s = ? and %s = ?", OWicketData.CLASS_NAME,
                    OWicketData.PROP_SESSION_ID, OWicketData.PROP_ID);
            db.command(sql, sessionId, id).close();
        });
    }

    @Override
    public void removeData(String sessionId) {
        DBClosure.sudoConsumer(db -> {
            String sql = String.format("delete from %s where %s = ?", OWicketData.CLASS_NAME, OWicketData.PROP_SESSION_ID);
            db.command(sql, sessionId).close();
        });
    }

    @Override
    public void storeData(String sessionId, int id, byte[] data) {
        Optional<OWicketData> wicketData = getWicketData(sessionId, id);
        if (wicketData.isPresent()) {
            DBClosure.sudoSave(wicketData.get().setData(data));
        } else DBClosure.sudoSave(new OWicketData(id, sessionId, data));
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public boolean isReplicated() {
        return true;
    }

    @Override
    public boolean canBeAsynchronous() {
        return false;
    }

    private Optional<OWicketData> getWicketData(String sessionId, int id) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where %s = ? and %s = ? limit 1", OWicketData.CLASS_NAME,
                    OWicketData.PROP_SESSION_ID, OWicketData.PROP_ID);
            try(OResultSet result = db.query(sql, sessionId, id)) {
            	return result.elementStream().findFirst().map(e -> new OWicketData((ODocument)e));
            }
        });
    }

}
