package org.orienteer.users.module;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.util.OUsersCommonUtils;

import static com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;


public class OrienteerUsersModule extends AbstractOrienteerModule {

    public static final String ORIENTEER_USER_ROLE = "orienteerUser";

    public static final String ORIENTEER_USER_PERSPECTIVE = "orienteerUserPerspective";

    public static final String MODULE_NAME = "orienteer-users";

    protected OrienteerUsersModule() {
        super(MODULE_NAME, 6,  PerspectivesModule.NAME);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);

        OClass userClass = helper.oClass(OrienteerUser.CLASS_NAME, OUser.CLASS_NAME)
                .oProperty(OrienteerUser.PROP_ID, OType.STRING).notNull().oIndex(OClass.INDEX_TYPE.UNIQUE)
                .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_RESTORE_ID, OType.STRING)
                .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_RESTORE_ID_CREATED, OType.DATETIME)
                .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .getOClass();

        ODocument perspective = createOrienteerUsersPerspective(db);

        OUsersCommonUtils.setRestricted(db, userClass);
        updateReaderPermissions(db);
        updateOrienteerUserRoleDoc(db, perspective);

//        adjustDefaultPerspective(db);
        return null;
    }


    private void updateOrienteerUserRoleDoc(ODatabaseDocument db, ODocument perspective) {
        OSecurity security = db.getMetadata().getSecurity();
        ORole role = security.getRole(ORIENTEER_USER_ROLE);
        if (role == null) {
            ORole reader = security.getRole("reader");
            role = security.createRole(ORIENTEER_USER_ROLE, reader, OSecurityRole.ALLOW_MODES.DENY_ALL_BUT);
        }

        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_WIDGET, 2);
        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_DASHBOARD, 2);

        role.grant(ResourceGeneric.CLASS, OrienteerUser.CLASS_NAME, 6);
        role.grant(ResourceGeneric.DATABASE, "cluster", 6);

        role.getDocument().field(PerspectivesModule.PROP_PERSPECTIVE, perspective);
        role.save();
    }

    private void updateReaderPermissions(ODatabaseDocument db) {
        ORole reader = db.getMetadata().getSecurity().getRole("reader");
        reader.grant(ResourceGeneric.CLASS, null, 0);
        reader.grant(ResourceGeneric.CLASS, ORole.CLASS_NAME, 0);
        reader.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_ITEM, 2);
        reader.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_PERSPECTIVE, 2);
        reader.save();
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

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }
}
