package org.orienteer.users.module;


import com.google.common.base.Strings;
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
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.module.PerspectivesModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.web.SearchPage;
import org.orienteer.core.web.schema.SchemaPage;
import org.orienteer.mail.OMailModule;
import org.orienteer.users.component.visualizer.OAuth2ProviderVisualizer;
import org.orienteer.users.hook.OrienteerUserHook;
import org.orienteer.users.hook.OrienteerUserRoleHook;
import org.orienteer.users.model.OAuth2Service;
import org.orienteer.users.model.OAuth2ServiceContext;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RegistrationResource;
import org.orienteer.users.resource.RestorePasswordResource;
import org.orienteer.users.util.OUsersCommonUtils;
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
    public static final int VERSION = 4;

    public static final CustomAttribute REMOVE_CRON_RULE              = CustomAttribute.create("remove.cron", OType.STRING, "", false, false);
    public static final CustomAttribute REMOVE_SCHEDULE_START_TIMEOUT = CustomAttribute.create("remove.timeout", OType.STRING, "0", false, false);

    public static final String MAIL_RESTORE      = "restore";
    public static final String MAIL_REGISTRATION = "registration";

    public static final String MAIL_MACROS_LINK = "link";

    protected OrienteerUsersModule() {
        super(MODULE_NAME, VERSION,  PerspectivesModule.NAME, OMailModule.NAME);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);

        OClass user = updateUserOClass(helper);

        updateDefaultOrienteerUsers(db);

        helper.oIndex(user.getProperty(OrienteerUser.PROP_ID).getFullName(), OClass.INDEX_TYPE.UNIQUE, OrienteerUser.PROP_ID);

        OUsersCommonUtils.setRestricted(db, helper.oClass(OIdentity.CLASS_NAME).getOClass());
        OUsersCommonUtils.setRestricted(db, helper.oClass(PerspectivesModule.OCLASS_PERSPECTIVE).getOClass());

        ODocument perspective = createOrienteerUsersPerspective(db);
        ODocument readerPerspective = createReaderPerspective(db);

        ODocument reader = updateAndGetUserReader(db);
        updateReaderPermissions(db, reader, readerPerspective);
        updateOrienteerUserRoleDoc(db, perspective);

        createRemoveRestoreIdFunction(helper);

        createOAuth2Services(helper);
        configureModuleClass(helper);

        return createModuleDocument(db);
    }

    private ODocument createModuleDocument(ODatabaseDocument db) {
        List<ODocument> docs = db.query(new OSQLSynchQuery<>("select from " + ModuleModel.CLASS_NAME, 1));

        if (docs != null && !docs.isEmpty()) {
            return null;
        }
        return new ODocument(ModuleModel.CLASS_NAME);
    }

    private void configureModuleClass(OSchemaHelper helper) {
        helper.oClass(ModuleModel.CLASS_NAME, IOrienteerModule.OMODULE_CLASS)
                .oProperty(ModuleModel.PROP_DOMAIN, OType.STRING, 40)
                    .notNull()
                    .defaultValue("http://localhost:8080")
                .oProperty(ModuleModel.PROP_OAUTH2, OType.BOOLEAN, 50)
                    .notNull()
                    .defaultValue("true")
                .oProperty(ModuleModel.PROP_OAUTH2_CALLBACK, OType.STRING, 60)
                    .notNull()
                    .defaultValue("/login");
    }

    private OClass updateUserOClass(OSchemaHelper helper) {
        helper.oClass(OUser.CLASS_NAME)
                .oProperty(OrienteerUser.PROP_ID, OType.STRING).notNull()
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_RESTORE_ID, OType.STRING)
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                    .updateCustomAttribute(REMOVE_CRON_RULE, "0 0/1 * * * ?")
                    .updateCustomAttribute(REMOVE_SCHEDULE_START_TIMEOUT, "86400000")
                .oProperty(OrienteerUser.PROP_RESTORE_ID_CREATED, OType.DATETIME)
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, true)
                .oProperty(OrienteerUser.PROP_EMAIL, OType.STRING)
                .oProperty(OrienteerUser.PROP_FIRST_NAME, OType.STRING)
                .oProperty(OrienteerUser.PROP_LAST_NAME, OType.STRING);

        return helper.getOClass();
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

        // TODO: remove this after release with fix for roles in OrientDB: https://github.com/orientechnologies/orientdb/issues/8338
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_ITEM, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_PERSPECTIVE, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, ORole.CLASS_NAME, READ.getPermissionFlag());
        role.grant(ResourceGeneric.SCHEMA, null, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLUSTER, "internal", READ.getPermissionFlag());
        role.grant(ResourceGeneric.RECORD_HOOK, "", READ.getPermissionFlag());
        role.grant(ResourceGeneric.DATABASE, null, READ.getPermissionFlag());
        role.grant(ResourceGeneric.DATABASE, "systemclusters", READ.getPermissionFlag());
        role.grant(ResourceGeneric.DATABASE, "function", READ.getPermissionFlag());
        role.grant(ResourceGeneric.DATABASE, "command", READ.getPermissionFlag());

        role.grant(OSecurityHelper.FEATURE_RESOURCE, SearchPage.SEARCH_FEATURE, READ.getPermissionFlag());

        role.grant(ResourceGeneric.CLASS, OrienteerUser.CLASS_NAME, OrientPermission.combinedPermission(READ, UPDATE));
        role.grant(ResourceGeneric.DATABASE, "cluster", OrientPermission.combinedPermission(READ, UPDATE));

        role.getDocument().field(ORestrictedOperation.ALLOW_READ.getFieldName(), Collections.singletonList(role.getDocument()));
        role.getDocument().field(PerspectivesModule.PROP_PERSPECTIVE, perspective);
        role.save();

        perspective.field(ORestrictedOperation.ALLOW_READ.getFieldName(), Collections.singletonList(role.getDocument()));
        perspective.save();
    }

    private void updateReaderPermissions(ODatabaseDocument db, ODocument reader, ODocument perspective) {
        ORole role = db.getMetadata().getSecurity().getRole("reader");
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_ITEM, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, PerspectivesModule.OCLASS_PERSPECTIVE, READ.getPermissionFlag());
        role.grant(ResourceGeneric.CLASS, null, 0);
        role.grant(ResourceGeneric.CLASS, ORole.CLASS_NAME, READ.getPermissionFlag());
        role.grant(OSecurityHelper.FEATURE_RESOURCE, SearchPage.SEARCH_FEATURE, 0);
        role.grant(OSecurityHelper.FEATURE_RESOURCE, SchemaPage.SCHEMA_FEATURE, 0);

        role.getDocument().field(ORestrictedOperation.ALLOW_READ.getFieldName(), Collections.singletonList(reader));
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

    private ODocument createReaderPerspective(ODatabaseDocument db) {
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

    private void createOAuth2Services(OSchemaHelper helper) {

        helper.oClass(OAuth2Service.CLASS_NAME)
                .oProperty(OAuth2Service.PROP_API_KEY, OType.STRING, 0)
                    .notNull()
                .oProperty(OAuth2Service.PROP_API_SECRET, OType.STRING, 10)
                    .notNull()
                .oProperty(OAuth2Service.PROP_PROVIDER, OType.STRING, 30)
                    .notNull()
                    .markAsDocumentName()
                    .assignVisualization(OAuth2ProviderVisualizer.NAME)
                    .updateCustomAttribute(CustomAttribute.DISPLAYABLE, "true")
                    .oIndex(OClass.INDEX_TYPE.UNIQUE);

        helper.oClass(OAuth2ServiceContext.CLASS_NAME)
                .oProperty(OAuth2ServiceContext.PROP_STATE, OType.STRING, 0)
                    .notNull()
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, "true")
                .oProperty(OAuth2ServiceContext.PROP_SERVICE, OType.LINK, 10)
                    .linkedClass(OAuth2Service.CLASS_NAME)
                    .notNull()
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, "true")
                .oProperty(OAuth2ServiceContext.PROP_USED, OType.BOOLEAN, 20)
                    .defaultValue("false")
                    .notNull()
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, "true")
                .oProperty(OAuth2ServiceContext.PROP_AUTHORIZATION_URL, OType.STRING, 30)
                    .notNull()
                    .updateCustomAttribute(CustomAttribute.UI_READONLY, "true");

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
        app.getUIVisualizersRegistry().registerUIComponentFactory(new OAuth2ProviderVisualizer());

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
        RestorePasswordResource.unmount(app);

        app.unmountPages("org.orienteer.users.web");
        app.getUIVisualizersRegistry().unregisterUIComponentFactory(Collections.singletonList(OType.STRING), OAuth2ProviderVisualizer.NAME);
    }


    public static class ModuleModel extends ODocumentWrapper {

        public static final String CLASS_NAME = "OrienteerUsers";

        public static final String PROP_DOMAIN          = "domain";
        public static final String PROP_OAUTH2          = "oauth2";
        public static final String PROP_OAUTH2_CALLBACK = "oauth2Callback";

        public ModuleModel() {
            this(CLASS_NAME);
        }

        public ModuleModel(String iClassName) {
            super(iClassName);
        }

        public ModuleModel(ODocument iDocument) {
            super(iDocument);
        }

        public String getDomain() {
            return document.field(PROP_DOMAIN);
        }

        public ModuleModel setDomain(String domain) {
            document.field(PROP_DOMAIN, domain);
            return this;
        }

        public boolean isOAuth2() {
            return document.field(PROP_OAUTH2);
        }

        public ModuleModel setOAuth2(boolean oauth2) {
            document.field(PROP_OAUTH2, oauth2);
            return this;
        }

        public String getOAuth2Callback() {
            return document.field(PROP_OAUTH2_CALLBACK);
        }

        public ModuleModel setOAuth2Callback(String callback) {
            document.field(PROP_OAUTH2_CALLBACK, callback);
            return this;
        }

        public String getFullOAuth2Callback() {
            String domain = getDomain();
            String callback = getOAuth2Callback();

            if (Strings.isNullOrEmpty(domain)) {
                return null;
            }

            if (!domain.endsWith("/")) {
                domain += "/";
            }

            if (Strings.isNullOrEmpty(callback)) {
                return domain;
            }

            if (callback.startsWith("/")) {
                callback = callback.substring(1);
            }

            return domain + callback;
        }
    }
}
