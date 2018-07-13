package org.orienteer.users.module;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.*;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.web.SearchPage;
import org.orienteer.core.web.schema.SchemaPage;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.util.OUsersCommonUtils;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;


/**
 * Orienteer module to setup DB model 
 */
public class OrienteerUsersModule extends AbstractOrienteerModule {

    public static final String ORIENTEER_USER_ROLE = "orienteerUser";

    public static final String ORIENTEER_USER_PERSPECTIVE = "orienteerUserPerspective";
    public static final String READER_PERSPECTIVE         = "readerPerspective";

    public static final String MODULE_NAME = "orienteer-users";

    protected OrienteerUsersModule() {
        super(MODULE_NAME, 1,  PerspectivesModule.NAME);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);

        helper.oClass(OUser.CLASS_NAME)
                .oProperty(OrienteerUser.PROP_ID, OType.STRING).notNull().oIndex(OClass.INDEX_TYPE.UNIQUE)
                .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_RESTORE_ID, OType.STRING)
                .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_RESTORE_ID_CREATED, OType.DATETIME)
                .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .getOClass();

        ODocument perspective = createOrienteerUsersPerspective(db);
        ODocument readerPerspective = createReaderPespective(db);

        OUsersCommonUtils.setRestricted(db, helper.oClass(OIdentity.CLASS_NAME).getOClass());
        OUsersCommonUtils.setRestricted(db, helper.oClass(PerspectivesModule.OCLASS_PERSPECTIVE).getOClass());

        ODocument reader = updateAndGetUserReader(db);
        updateReaderPermissions(db, reader, readerPerspective);
        updateOrienteerUserRoleDoc(db, perspective);


        return null;
    }


    private void updateOrienteerUserRoleDoc(ODatabaseDocument db, ODocument perspective) {
        OSecurity security = db.getMetadata().getSecurity();
        ORole role = security.getRole(ORIENTEER_USER_ROLE);
        if (role == null) {
            ORole reader = security.getRole("reader");
            role = security.createRole(ORIENTEER_USER_ROLE, reader, OSecurityRole.ALLOW_MODES.DENY_ALL_BUT);
        }

        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_WIDGET, OrientPermission.READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_DASHBOARD, OrientPermission.READ.getPermissionFlag());

        role.grant(OSecurityHelper.FEATURE_RESOURCE, SearchPage.SEARCH_FEATURE, OrientPermission.READ.getPermissionFlag());

        role.grant(ResourceGeneric.CLASS, OrienteerUser.CLASS_NAME, OrientPermission.UPDATE.getPermissionFlag());
        role.grant(ResourceGeneric.DATABASE, "cluster", OrientPermission.UPDATE.getPermissionFlag());


        role.getDocument().field(PerspectivesModule.PROP_PERSPECTIVE, perspective);
        role.save();
    }

    private void updateReaderPermissions(ODatabaseDocument db, ODocument reader, ODocument perspective) {
        ORole role = db.getMetadata().getSecurity().getRole("reader");
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_ITEM, OrientPermission.READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_PERSPECTIVE, OrientPermission.READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, null, 0);
        role.grant(ResourceGeneric.CLASS, ORole.CLASS_NAME, 0);
        role.grant(OSecurityHelper.FEATURE_RESOURCE, SearchPage.SEARCH_FEATURE, 0);
        role.grant(OSecurityHelper.FEATURE_RESOURCE, SchemaPage.SCHEMA_FEATURE, 0);
        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_DASHBOARD, 0);

        role.getDocument().field(ORestrictedOperation.ALLOW_READ.getFieldName(), Arrays.asList(reader, perspective));
        role.getDocument().field(PerspectivesModule.PROP_PERSPECTIVE, perspective);

        role.save();


        perspective.field(ORestrictedOperation.ALLOW_READ.getFieldName(), Collections.singleton(role.getDocument()));
        perspective.save();
    }

    private ODocument createOrienteerUsersPerspective(ODatabaseDocument db) {
        ODocument perspective = OUsersCommonUtils.getOrCreatePerspective(db, ORIENTEER_USER_PERSPECTIVE);
        perspective.field("icon", FAIconType.user_o.name());
        perspective.field("homeUrl", "/browse/" + OrienteerUser.CLASS_NAME);
        perspective.save();

        ODocument item1 = OUsersCommonUtils.getOrCreatePerspectiveItem(db, perspective, "perspective.menu.item.profile");
        item1.field("icon", FAIconType.user_o.name());
        item1.field("perspective", perspective);
        item1.field("url", "/browse/" + OrienteerUser.CLASS_NAME); // TODO: add macros for links
        item1.save();

        perspective.save();

        return perspective;
    }

    private ODocument createReaderPespective(ODatabaseDocument db) {
        ODocument perspective = OUsersCommonUtils.getOrCreatePerspective(db, READER_PERSPECTIVE);
        perspective.field("icon", FAIconType.database.name());
        perspective.field("homeUrl", "/browse/" + OUser.CLASS_NAME);
        perspective.save();

        return perspective;
    }

    private ODocument updateAndGetUserReader(ODatabaseDocument db) {
        String sql = String.format("select from %s where name = ?", OUser.CLASS_NAME);
        List<ODocument> docs = db.query(new OSQLSynchQuery<>(sql, 1), "reader");
        ODocument reader = docs.get(0);
        Set<OIdentifiable> users = reader.field(ORestrictedOperation.ALLOW_READ.getFieldName(), Set.class);
        if (users == null || users.isEmpty()) {
            reader.field(ORestrictedOperation.ALLOW_READ.getFieldName(), Collections.singleton(reader));
        } else {
            users.add(reader);
            reader.field(ORestrictedOperation.ALLOW_READ.getFieldName(), users);
        }
        reader.save();
        return reader;
    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }
}
