package org.orienteer.users.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.module.OrienteerUsersModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.orienteer.core.util.CommonUtils.getDocument;
import static org.orienteer.core.util.CommonUtils.getFromIdentifiables;

/**
 * Specialized utils for DB
 */
public final class OUsersDbUtils {
	
	private OUsersDbUtils() {
		
	}

    public static Optional<ODocument> getDefaultOrienteerUserPerspective() {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where name.en = ?", PerspectivesModule.OCLASS_PERSPECTIVE);
            String name = OUsersCommonUtils.getString(OrienteerWebApplication.lookupApplication(), OrienteerUsersModule.ORIENTEER_USER_PERSPECTIVE, Locale.ENGLISH);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
            return getDocument(identifiables);
        });
    }

    public static Optional<ORole> getRoleByName(String name) {
        return DBClosure.sudo(db -> ofNullable(db.getMetadata().getSecurity().getRole(name)));
    }

    public static Optional<OrienteerUser> getUserByRestoreId(String id) {
	    return getUserBy(OrienteerUser.PROP_RESTORE_ID, id);
    }

    public static Optional<OrienteerUser> getUserById(String id) {
	    return getUserBy(OrienteerUser.PROP_ID, id);
    }

    public static boolean isUserExistsWithRestoreId(String id) {
	    return DBClosure.sudo(db -> {
	        String sql = String.format("select count(*) from %s where %s = ?", OrienteerUser.CLASS_NAME, OrienteerUser.PROP_RESTORE_ID);
	        List<OIdentifiable> documents = db.query(new OSQLSynchQuery<>(sql, 1), id);
	        return getDocument(documents)
                    .map(d -> (boolean) d.field("count"))
                    .orElse(false);
        });
    }

    private static Optional<OrienteerUser> getUserBy(String field, String value) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where %s = ?", OrienteerUser.CLASS_NAME, field);
            List<OIdentifiable> identifiables  = db.query(new OSQLSynchQuery<>(sql, 1), value);
            return getFromIdentifiables(identifiables, OrienteerUser::new);
        });
    }
}
