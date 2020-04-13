package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.module.OrienteerUsersModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.orienteer.core.util.CommonUtils.getFromIdentifiable;

/**
 * Repository for work with user models
 */
public final class OrienteerUserRepository {

    private OrienteerUserRepository() {}

    /**
     * @return optional which contains perspective {@link OrienteerUsersModule#ORIENTEER_USER_PERSPECTIVE}
     */
    public static Optional<ODocument> getDefaultOrienteerUserPerspective() {
        return DBClosure.sudo(db -> {
            PerspectivesModule perspectivesModule = OrienteerWebApplication.lookupApplication().getServiceInstance(PerspectivesModule.class);
            return perspectivesModule.getPerspectiveByAliasAsDocument(db, OrienteerUsersModule.ORIENTEER_USER_PERSPECTIVE);
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
     * Check if user exists with given field and value
     * @param field field
     * @param value value
     * @return true if user with given field and value exists in database
     */
    private static boolean isUserExistsBy(String field, String value) {
        return DBClosure.sudo(db -> {
            String sql = String.format("select count(*) as count from %s where %s = ? limit 1", OrienteerUser.CLASS_NAME, field);
            return db.query(sql, 1, value).elementStream()
                    .anyMatch(d -> (long) d.getProperty("count") == 1);
        });
    }

    /**
     * Search user by given field and value
     * @param field field
     * @param value value
     * @return user which field contains given value
     */
    public static Optional<OrienteerUser> getUserBy(String field, String value) {
        return DBClosure.sudo(db -> getUserBy(db, field, value));
    }

    public static Optional<OrienteerUser> getUserBy(ODatabaseDocument db, String field, String value) {
        String sql = String.format("select from %s where %s = ? limit 1", OrienteerUser.CLASS_NAME, field);
        return db.query(sql, value).elementStream()
                .map(e -> getFromIdentifiable(e.getRecord(), OrienteerUser::new).orElse(null))
                .findFirst();
    }
}
