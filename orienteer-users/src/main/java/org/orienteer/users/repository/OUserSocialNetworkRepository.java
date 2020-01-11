package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.model.IOAuth2Provider;
import org.orienteer.users.model.OUserSocialNetwork;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

/**
 * Repository for access {@link OUserSocialNetwork}
 */
public final class OUserSocialNetworkRepository {

    private OUserSocialNetworkRepository() {}

    public static Optional<OUserSocialNetwork> getSocialNetworkByUserId(IOAuth2Provider provider, String userId) {
        return getSocialNetworkByUserId(provider.getName(), userId);
    }

    public static Optional<OUserSocialNetwork> getSocialNetworkByUserId(String providerAlias, String userId) {
        return DBClosure.sudo(db -> getSocialNetworkByUserId(db, providerAlias, userId));
    }

    public static Optional<OUserSocialNetwork> getSocialNetworkByUserId(ODatabaseDocument db, IOAuth2Provider provider, String userId) {
        return getSocialNetworkByUserId(db, provider.getName(), userId);
    }

    public static Optional<OUserSocialNetwork> getSocialNetworkByUserId(ODatabaseDocument db, String providerAlias, String userId) {
        String sql = String.format("select from %s where %s = ? and %s = ?", OUserSocialNetwork.CLASS_NAME,
                OUserSocialNetwork.PROP_SERVICE, OUserSocialNetwork.PROP_USER_ID);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), providerAlias, userId);
        return CommonUtils.getFromIdentifiables(identifiables, OUserSocialNetwork::new);
    }
}
