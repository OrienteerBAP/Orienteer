package org.orienteer.core.service;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.eclipse.jetty.server.session.SessionData;
import org.orienteer.core.module.JettySesssionModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.jetty.ISessionDataStore;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrientDbSessionDataStore implements ISessionDataStore {

    @Override
    public void store(String id, SessionData data, long lastSaveTime) {
        DBClosure.sudoConsumer(db -> {
            String sql = createStoreSessionDataSql(db, id);
            db.command(new OCommandSQL(sql))
                    .execute(
                        data.getContextPath(),
                        data.getVhost(),
                        data.getLastNode(),
                        data.getExpiry(),
                        data.getCreated(),
                        data.getCookieSet(),
                        data.getAccessed(),
                        data.getLastAccessed(),
                        data.getMaxInactiveMs(),
                        data.getAllAttributes(),
                        data.isDirty(),
                        data.getLastSaved(),
                        data.getId()
                    );
        });


    }

    @Override
    public Set<String> getExpired(Set<String> candidates) {
        return candidates;
    }

    @Override
    public boolean isPassivating() {
        return true;
    }

    @Override
    public boolean exists(String id) {
        return DBClosure.sudo(db -> isExists(db, id));
    }

    @Override
    public SessionData load(String id) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where %s = ?", JettySesssionModule.SESSION_DATA, JettySesssionModule.PROP_ID);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), id);
            return CommonUtils.getFromIdentifiables(identifiables, this::toSessionData).orElse(null);
        });
    }

    @Override
    public boolean delete(String id) {
        return DBClosure.sudo(db -> {
            String sql = String.format("delete from %s where %s = ?", JettySesssionModule.SESSION_DATA, JettySesssionModule.PROP_ID);
            List<?> list = db.command(new OCommandSQL(sql)).execute(id);
            return list != null && !list.isEmpty();
        });
    }

    private boolean isExists(ODatabaseDocument db, String id) {
        String sql = String.format("select %s from %s where %s = ?", JettySesssionModule.PROP_ID,
                JettySesssionModule.SESSION_DATA, JettySesssionModule.PROP_ID);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), id);
        return identifiables != null && !identifiables.isEmpty();
    }


    private String createStoreSessionDataSql(ODatabaseDocument db, String id) {
        StringBuilder sb = new StringBuilder();
        if (isExists(db, id)) {
            sb.append("update ")
                    .append(JettySesssionModule.SESSION_DATA)
                    .append(" set ");
            updateMainFields(sb)
                    .append(" where ")
                    .append(JettySesssionModule.PROP_ID)
                    .append(" = ?");
        } else {
            sb.append("insert into ")
                    .append(JettySesssionModule.SESSION_DATA)
                    .append(" set ");
            updateMainFields(sb)
                    .append(",")
                    .append(JettySesssionModule.PROP_ID)
                    .append(" = ?");
        }
        return sb.toString();
    }

    private StringBuilder updateMainFields(StringBuilder sb) {
        return sb.append(JettySesssionModule.PROP_CONTEXT_PATH)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_VHOST)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_LAST_NODE)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_EXPIRY)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_CREATED)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_COOKIE_SET)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_ACCESSED)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_MAX_INACTIVE_MS)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_ATTRIBUTES)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_DIRTY)
                .append(" = ?, ")
                .append(JettySesssionModule.PROP_LAST_SAVED)
                .append(" = ? ");
    }

    private SessionData toSessionData(ODocument doc) {
        SessionData sessionData = new SessionData(
                doc.field(JettySesssionModule.PROP_ID),
                doc.field(JettySesssionModule.PROP_CONTEXT_PATH),
                doc.field(JettySesssionModule.PROP_VHOST),
                doc.field(JettySesssionModule.PROP_CREATED),
                doc.field(JettySesssionModule.PROP_ACCESSED),
                doc.field(JettySesssionModule.PROP_LAST_ACCESSED),
                doc.field(JettySesssionModule.PROP_MAX_INACTIVE_MS),
                doc.field(JettySesssionModule.PROP_ATTRIBUTES, Map.class)
        );
        sessionData.setCookieSet(doc.field(JettySesssionModule.PROP_COOKIE_SET));
        sessionData.setDirty(doc.field(JettySesssionModule.PROP_DIRTY));
        sessionData.setLastSaved(doc.field(JettySesssionModule.PROP_LAST_SAVED));
        return sessionData;
    }
}
