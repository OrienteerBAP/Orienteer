package org.orienteer.module;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.function.OFunctionLibrary;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.*;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.schedule.OScheduledEvent;
import com.orientechnologies.orient.core.schedule.OScheduler;
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
import org.orienteer.resource.RegistrationResource;
import org.orienteer.resource.RestorePasswordResource;
import org.orienteer.hook.OrienteerUserHook;
import org.orienteer.hook.OrienteerUserRoleHook;
import org.orienteer.model.OrienteerUser;
import org.orienteer.util.OUsersCommonUtils;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import java.util.*;

import static com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;
import static ru.ydn.wicket.wicketorientdb.security.OrientPermission.READ;
import static ru.ydn.wicket.wicketorientdb.security.OrientPermission.UPDATE;


/**
 * Orienteer module to setup DB model 
 */
public class OrienteerUsersModule extends AbstractOrienteerModule {

    public static final String ORIENTEER_USER_ROLE = "orienteerUser";

    public static final String ORIENTEER_USER_PERSPECTIVE = "orienteerUserPerspective";
    public static final String READER_PERSPECTIVE         = "readerPerspective";

    public static final String EVENT_RESTORE_PASSWORD_PREFIX = "removeUserRestoreId";

    public static final String FUN_REMOVE_RESTORE_ID = "removeRestoreId";
    public static final String PARAM_RESTORE_ID      = "restoreId";
    public static final String PARAM_EVENT_NAME      = "eventName";
    public static final String PARAM_TIMEOUT         = "timeout";

    public static final String MODULE_NAME = "orienteer-users";

    public static final CustomAttribute REMOVE_CRON_RULE              = CustomAttribute.create("remove.cron", OType.STRING, "", false, false);
    public static final CustomAttribute REMOVE_SCHEDULE_START_TIMEOUT = CustomAttribute.create("remove.timeout", OType.STRING, "0", false, false);

    public static final String MAIL_RESTORE      = "restore";
    public static final String MAIL_REGISTRATION = "registration";

    public static final String MAIL_MACROS_LINK = "link";

    protected OrienteerUsersModule() {
        super(MODULE_NAME, 4,  PerspectivesModule.NAME);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);

        OClass user = helper.oClass(OUser.CLASS_NAME)
                .oProperty(OrienteerUser.PROP_ID, OType.STRING).notNull()
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_RESTORE_ID, OType.STRING)
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                    .updateCustomAttribute(REMOVE_CRON_RULE, "0 0/1 * * * ?")
                    .updateCustomAttribute(REMOVE_SCHEDULE_START_TIMEOUT, "86400000")
                .oProperty(OrienteerUser.PROP_RESTORE_ID_CREATED, OType.DATETIME)
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_EMAIL, OType.STRING).notNull()
                .getOClass();

        updateDefaultOrienteerUsers(db);

        helper.oIndex(user.getProperty(OrienteerUser.PROP_ID).getFullName(), OClass.INDEX_TYPE.UNIQUE);
        helper.oIndex(user.getProperty(OrienteerUser.PROP_EMAIL).getFullName(), OClass.INDEX_TYPE.UNIQUE);

        ODocument perspective = createOrienteerUsersPerspective(db);
        ODocument readerPerspective = createReaderPespective(db);

        OUsersCommonUtils.setRestricted(db, helper.oClass(OIdentity.CLASS_NAME).getOClass());
        OUsersCommonUtils.setRestricted(db, helper.oClass(PerspectivesModule.OCLASS_PERSPECTIVE).getOClass());

        ODocument reader = updateAndGetUserReader(db);
        updateReaderPermissions(db, reader, readerPerspective);
        updateOrienteerUserRoleDoc(db, perspective);

        createRemoveRestoreIdFunction(helper);


        return null;
    }


    private void updateOrienteerUserRoleDoc(ODatabaseDocument db, ODocument perspective) {
        OSecurity security = db.getMetadata().getSecurity();
        ORole role = security.getRole(ORIENTEER_USER_ROLE);
        if (role == null) {
            ORole reader = security.getRole("reader");
            role = security.createRole(ORIENTEER_USER_ROLE, reader, OSecurityRole.ALLOW_MODES.DENY_ALL_BUT);
        }

        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_WIDGET, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, OWidgetsModule.OCLASS_DASHBOARD, READ.getPermissionFlag());

        role.grant(OSecurityHelper.FEATURE_RESOURCE, SearchPage.SEARCH_FEATURE, READ.getPermissionFlag());

        role.grant(ResourceGeneric.CLASS, OrienteerUser.CLASS_NAME, OrientPermission.combinedPermission(READ, UPDATE));
        role.grant(ResourceGeneric.DATABASE, "cluster", OrientPermission.combinedPermission(READ, UPDATE));


        role.getDocument().field(PerspectivesModule.PROP_PERSPECTIVE, perspective);
        role.save();
    }

    private void updateReaderPermissions(ODatabaseDocument db, ODocument reader, ODocument perspective) {
        ORole role = db.getMetadata().getSecurity().getRole("reader");
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_ITEM, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_PERSPECTIVE, READ.getPermissionFlag());
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

    /**
     * Create function which will remove user restoreId by scheduler
     * @param helper {@link OSchemaHelper} Orienteer helper
     */
    private void createRemoveRestoreIdFunction(OSchemaHelper helper) {
        OFunctionLibrary lib = helper.getDatabase().getMetadata().getFunctionLibrary();
        if (lib.getFunction(FUN_REMOVE_RESTORE_ID) != null) {
            lib.dropFunction(FUN_REMOVE_RESTORE_ID);
        }
        OFunction function = lib.createFunction(FUN_REMOVE_RESTORE_ID);
        function.setName(FUN_REMOVE_RESTORE_ID);
        function.setLanguage("javascript");
        function.setCode(createCodeForRemoveRestoreIdFunction());
        function.setParameters(createParamsForRemoveRestoreIdFunction());
        function.save();
    }

    private String createCodeForRemoveRestoreIdFunction() {
        return String.format("var res = db.command('UPDATE %s SET %s = null, %s = null WHERE %s = ? AND %s <= (sysdate() - ?)', %s, %s);\n"
                        + "if (res > 0) db.command('DELETE FROM OSchedule WHERE name = ?', %s);",
                OrienteerUser.CLASS_NAME, OrienteerUser.PROP_RESTORE_ID, OrienteerUser.PROP_RESTORE_ID_CREATED,
                OrienteerUser.PROP_RESTORE_ID,
                OrienteerUser.PROP_RESTORE_ID_CREATED,
                PARAM_RESTORE_ID,
                PARAM_TIMEOUT,
                PARAM_EVENT_NAME
        );
    }

    private List<String> createParamsForRemoveRestoreIdFunction() {
        List<String> params = new LinkedList<>();
        params.add(PARAM_RESTORE_ID);
        params.add(PARAM_EVENT_NAME);
        params.add(PARAM_TIMEOUT);
        return params;
    }

    private void updateDefaultOrienteerUsers(ODatabaseDocument db) {
        OSecurity security = db.getMetadata().getSecurity();

        final ODocument admin = security.getUser("admin").getDocument();
        admin.field(OrienteerUser.PROP_ID, UUID.randomUUID().toString());
        admin.field(OrienteerUser.PROP_EMAIL, "admin@gmail.com");
        admin.save();

        final ODocument reader = security.getUser("reader").getDocument();
        reader.field(OrienteerUser.PROP_ID, UUID.randomUUID().toString());
        reader.field(OrienteerUser.PROP_EMAIL, "reader@gmail.com");
        reader.save();

        final ODocument writer = security.getUser("writer").getDocument();
        writer.field(OrienteerUser.PROP_ID, UUID.randomUUID().toString());
        writer.field(OrienteerUser.PROP_EMAIL, "writer@gmail.com");
        writer.save();
    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
        hooks.add(OrienteerUserHook.class);
        hooks.add(OrienteerUserRoleHook.class);

        RegistrationResource.mount(app);
        RestorePasswordResource.mount(app);

        app.mountPages("org.orienteer.users.web");

        OScheduler scheduler = db.getMetadata().getScheduler();
        Collection<OScheduledEvent> events = scheduler.getEvents().values(); // TODO: remove after fix issue https://github.com/orientechnologies/orientdb/issues/8368
        for (OScheduledEvent event : events) {
            scheduler.updateEvent(event);
        }
    }

    @Override
    public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
        List<Class<? extends ORecordHook>> hooks = app.getOrientDbSettings().getORecordHooks();
        hooks.remove(OrienteerUserHook.class);
        hooks.remove(OrienteerUserRoleHook.class);

        RegistrationResource.unmount(app);
        RestorePasswordResource.mount(app);

        app.unmountPages("org.orienteer.users.web");
    }
}
