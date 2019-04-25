package org.orienteer.users.repository;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.module.OrienteerUsersModule;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;
import java.util.Optional;

public final class OrienteerUserModuleRepository {

    public static Optional<OrienteerUsersModule.ModuleModel> getModuleModel() {
        return DBClosure.sudo(OrienteerUserModuleRepository::getModuleModel);
    }

    public static Optional<OrienteerUsersModule.ModuleModel> getModuleModel(ODatabaseDocument db) {
        String sql = String.format("select from %s where %s = true",
                OrienteerUsersModule.ModuleModel.CLASS_NAME, IOrienteerModule.OMODULE_ACTIVATE);
        List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql), 1);
        return CommonUtils.getFromIdentifiables(identifiables, OrienteerUsersModule.ModuleModel::new);
    }
}
