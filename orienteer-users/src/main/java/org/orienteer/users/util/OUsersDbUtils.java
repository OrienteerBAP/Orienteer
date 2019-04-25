package org.orienteer.users.util;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
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
	
	private OUsersDbUtils() {}

    /**
     * @return optional which contains perspective {@link OrienteerUsersModule#ORIENTEER_USER_PERSPECTIVE}
     */
    public static Optional<ODocument> getDefaultOrienteerUserPerspective() {
        return DBClosure.sudo(db -> {
            String sql = String.format("select from %s where name.en = ?", PerspectivesModule.OCLASS_PERSPECTIVE);
            String name = OUsersCommonUtils.getString(OrienteerWebApplication.lookupApplication(), OrienteerUsersModule.ORIENTEER_USER_PERSPECTIVE, Locale.ENGLISH);
            List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), name);
            return getDocument(identifiables);
        });
    }

    /**
     * @param name {@link String} role name
     * @return role
     */
    public static Optional<ORole> getRoleByName(String name) {
        return DBClosure.sudo(db -> ofNullable(db.getMetadata().getSecurity().getRole(name)));
    }

    /**
     * Search user by given restore id
     * @param restoreId {@link String} restore id
     * @return user with given restore id
     */
    public static Optional<OrienteerUser> getUserByRestoreId(String restoreId) {
	    return getUserBy(OrienteerUser.PROP_RESTORE_ID, restoreId);
    }

    /**
     * Search user by given id
     * @param id user id
     * @return user with given id
     */
    public static Optional<OrienteerUser> getUserById(String id) {
	    return getUserBy(OrienteerUser.PROP_ID, id);
    }

    /**
     * Search user by given email
     * @param email user email
     * @return user with given email
     */
    public static Optional<OrienteerUser> getUserByEmail(String email) {
        return getUserBy(OrienteerUser.PROP_EMAIL, email);
    }

    public static Optional<OrienteerUser> getUserByEmail(ODatabaseDocument db, String email) {
        return getUserBy(db, OrienteerUser.PROP_EMAIL, email);
    }

    public static Optional<OrienteerUser> getUserByName(ODatabaseDocument db, String name) {
        return getUserBy(db, "name", name);
    }

    public static Optional<OrienteerUsersModule.ModuleModel> getModuleModel() {
        return DBClosure.sudo(OUsersDbUtils::getModuleModel);
    }

    public static Optional<OrienteerUsersModule.ModuleModel> getModuleModel(ODatabaseDocument db) {
        String sql = String.format("select from %s where %s = true",
                OrienteerUsersModule.ModuleModel.CLASS_NAME, IOrienteerModule.OMODULE_ACTIVATE);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql), 1);
        return CommonUtils.getFromIdentifiables(identifiables, OrienteerUsersModule.ModuleModel::new);
    }

    public static List<OAuth2Service> getOAuth2Services() {
        return DBClosure.sudo(OUsersDbUtils::getOAuth2Services);
    }

    public static List<OAuth2Service> getOAuth2Services(ODatabaseDocument db) {
        String sql = String.format("select from %s", OAuth2Service.CLASS_NAME);
        List<OIdentifiable> services = db.query(new OSQLSynchQuery<>(sql));
        return CommonUtils.mapIdentifiables(services, OAuth2Service::new);
    }

    public static Optional<OAuth2ServiceContext> getServiceContextByState(String state) {
        return DBClosure.sudo(db -> OUsersDbUtils.getServiceContextByState(db, state));
    }

    public static Optional<OAuth2ServiceContext> getServiceContextByState(ODatabaseDocument db, String state) {
        String sql = String.format("select from %s where %s = ?",
                OAuth2ServiceContext.CLASS_NAME, OAuth2ServiceContext.PROP_STATE);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), state);
        return CommonUtils.getFromIdentifiables(identifiables, OAuth2ServiceContext::new);
    }

    /**
     * Check if user exists with given restore id
     * @param restoreId user restore id
     * @return true if user with given restoreId exists in database
     */
    public static boolean isUserExistsWithRestoreId(String restoreId) {
	    return isUserExistsBy(OrienteerUser.PROP_RESTORE_ID, restoreId);
    }

    /**
     * Check if user exists with given email
     * @param email user email
     * @return true if user exists with given email
     */
    public static boolean isUserExistsWithEmail(String email) {
        return isUserExistsBy(OrienteerUser.PROP_EMAIL, email);
    }

    /**
     * Check if user exists with given id
     * @param id user id
     * @return true if user exists with given id
     */
    public static boolean isUserExistsWithId(String id) {
        return isUserExistsBy(OrienteerUser.PROP_ID, id);
    }

    /**
     * Search user by given field and value
     * @param field field
     * @param value value
     * @return user which field contains given value
     */
    private static Optional<OrienteerUser> getUserBy(String field, String value) {
        return DBClosure.sudo(db -> getUserBy(db, field, value));
    }

    private static Optional<OrienteerUser> getUserBy(ODatabaseDocument db, String field, String value) {
        String sql = String.format("select from %s where %s = ?", OrienteerUser.CLASS_NAME, field);
        List<OIdentifiable> identifiables  = db.query(new OSQLSynchQuery<>(sql, 1), value);
        return getFromIdentifiables(identifiables, OrienteerUser::new);
    }

    /**
     * Check if user exists with given field and value
     * @param field field
     * @param value value
     * @return true if user with given field and value exists in database
     */
    private static boolean isUserExistsBy(String field, String value) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select count(*) from %s where %s = ?", OrienteerUser.CLASS_NAME, field);
            List<OIdentifiable> documents = db.query(new OSQLSynchQuery<>(sql, 1), value);
            return getDocument(documents)
                    .map(d -> (long) d.field("count") == 1)
                    .orElse(false);
        });
    }
}
