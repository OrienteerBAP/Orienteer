package org.orienteer.users.util;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.module.OrienteerUsersModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Locale;

/**
 * Specialized utils for DB
 */
public final class OUsersDbUtils {
	
	private OUsersDbUtils() {
		
	}

    public static ODocument getDefaultOrienteerUserPerspective() {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where name.en = ?", PerspectivesModule.OCLASS_PERSPECTIVE);
            String name = OUsersCommonUtils.getString(OrienteerWebApplication.lookupApplication(), OrienteerUsersModule.ORIENTEER_USER_PERSPECTIVE, Locale.ENGLISH);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
            return CommonUtils.getDocument(identifiables);
        });
    }

    public static ORole getRoleByName(String name) {
        return DBClosure.sudo(db -> db.getMetadata().getSecurity().getRole(name));
    }
}
