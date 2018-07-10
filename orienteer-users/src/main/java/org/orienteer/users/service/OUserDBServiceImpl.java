package org.orienteer.users.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.service.IDBService;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.util.OUsersCommonUtils;

import java.util.List;
import java.util.Locale;

@Singleton
public class OUserDBServiceImpl implements IOUsersDBService {

    @Inject
    private IDBService dbService;

    @Override
    public ODocument getDefaultOrienteerUserPerspective() {
        String sql = String.format("select from %s where name.en = ?", PerspectivesModule.OCLASS_PERSPECTIVE);
        String name = OUsersCommonUtils.getString(OrienteerWebApplication.lookupApplication(), OrienteerUsersModule.ORIENTEER_USER_PERSPECTIVE, Locale.ENGLISH);
        List<OIdentifiable> identifiables = dbService.query(new OSQLSynchQuery<>(sql, 1), name);
        return CommonUtils.getDocument(identifiables);
    }

    @Override
    public ORole getRoleByName(String name) {
        return dbService.sudo(db -> db.getMetadata().getSecurity().getRole(name));
    }
}
