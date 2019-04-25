package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

/**
 * Repository for work with OAuth2 models
 */
public final class OAuth2Repository {

    private OAuth2Repository() {}

    public static List<OAuth2Service> getOAuth2Services() {
        return DBClosure.sudo(OAuth2Repository::getOAuth2Services);
    }

    public static List<OAuth2Service> getOAuth2Services(ODatabaseDocument db) {
        String sql = String.format("select from %s", OAuth2Service.CLASS_NAME);
        List<OIdentifiable> services = db.query(new OSQLSynchQuery<>(sql));
        return CommonUtils.mapIdentifiables(services, OAuth2Service::new);
    }

    public static Optional<OAuth2ServiceContext> getServiceContextByState(String state) {
        return DBClosure.sudo(db -> OAuth2Repository.getServiceContextByState(db, state));
    }

    public static Optional<OAuth2ServiceContext> getServiceContextByState(ODatabaseDocument db, String state) {
        String sql = String.format("select from %s where %s = ?",
                OAuth2ServiceContext.CLASS_NAME, OAuth2ServiceContext.PROP_STATE);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), state);
        return CommonUtils.getFromIdentifiables(identifiables, OAuth2ServiceContext::new);
    }

}
