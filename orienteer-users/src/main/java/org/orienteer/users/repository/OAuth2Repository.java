package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.orienteer.users.repository.DatabaseHelper.selectFromBy;

/**
 * Repository for work with OAuth2 models
 */
public final class OAuth2Repository {

    private OAuth2Repository() {}

    public static List<OAuth2Service> getOAuth2Services() {
        return DBClosure.sudo(OAuth2Repository::getOAuth2Services);
    }

    public static List<OAuth2Service> getOAuth2Services(boolean active) {
        return DBClosure.sudo(db -> getOAuth2Services(db, active));
    }

    public static List<OAuth2Service> getOAuth2Services(ODatabaseDocument db) {
        String sql = String.format("select from %s", OAuth2Service.CLASS_NAME);
        return db.query(sql).elementStream()
                .map(element -> CommonUtils.getFromIdentifiable(element.getRecord(), OAuth2Service::new).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static List<OAuth2Service> getOAuth2Services(ODatabaseDocument db, boolean active) {
        String sql = selectFromBy(OAuth2Service.CLASS_NAME, OAuth2Service.PROP_ACTIVE);
        return db.query(sql, active).elementStream()
                .map(element -> CommonUtils.getFromIdentifiable(element.getRecord(), OAuth2Service::new).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static Optional<OAuth2ServiceContext> getServiceContextByState(String state) {
        return DBClosure.sudo(db -> OAuth2Repository.getServiceContextByState(db, state));
    }

    public static Optional<OAuth2ServiceContext> getServiceContextByState(ODatabaseDocument db, String state) {
        String sql = String.format("select from %s where %s = ?",
                OAuth2ServiceContext.CLASS_NAME, OAuth2ServiceContext.PROP_STATE);
        return db.query(sql, state).elementStream()
                .map(element -> CommonUtils.getFromIdentifiable(element.getRecord(), OAuth2ServiceContext::new).orElse(null))
                .findFirst();
    }

}
