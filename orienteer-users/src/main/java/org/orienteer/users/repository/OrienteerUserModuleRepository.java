package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.module.OrienteerUsersModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Optional;

/**
 * Repository for work with {@link OrienteerUsersModule.ModuleModel}
 */
public final class OrienteerUserModuleRepository {

    private OrienteerUserModuleRepository() {}

    public static Optional<OrienteerUsersModule.ModuleModel> getModuleModel() {
        return DBClosure.sudo(OrienteerUserModuleRepository::getModuleModel);
    }

    public static Optional<OrienteerUsersModule.ModuleModel> getModuleModel(ODatabaseDocument db) {
        String sql = String.format("select from %s where %s = true limit 1",
                OrienteerUsersModule.ModuleModel.CLASS_NAME, IOrienteerModule.OMODULE_ACTIVATE);
        return db.query(sql).elementStream()
                .map(element -> CommonUtils.getFromIdentifiable(element.getRecord(), OrienteerUsersModule.ModuleModel::new).orElse(null))
                .findFirst();
    }

    public static boolean isRegistrationActive() {
        return getModuleModel()
                .map(OrienteerUsersModule.ModuleModel::isRegistration)
                .orElseThrow(OrienteerUserModuleRepository::moduleNotConfiguredException);
    }

    public static boolean isOAuth2Active() {
        return getModuleModel()
                .map(OrienteerUsersModule.ModuleModel::isOAuth2)
                .orElseThrow(OrienteerUserModuleRepository::moduleNotConfiguredException);
    }

    public static boolean isRestorePassword() {
        return getModuleModel()
                .map(OrienteerUsersModule.ModuleModel::isRestorePassword)
                .orElseThrow(OrienteerUserModuleRepository::moduleNotConfiguredException);
    }

    private static IllegalStateException moduleNotConfiguredException() {
        return new IllegalStateException("There is no configured module - " + OrienteerUsersModule.ModuleModel.CLASS_NAME);
    }
}
