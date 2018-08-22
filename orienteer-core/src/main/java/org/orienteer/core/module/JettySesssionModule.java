package org.orienteer.core.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.service.OrientDbSessionDataStore;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.jetty.OrienteerSessionDataStoreFactory;
import org.orienteer.jetty.SessionDataStoreProxy;

public class JettySesssionModule extends AbstractOrienteerModule {


    public static final String SESSION_DATA = "SessionData";

    public static final String PROP_ID              = "id";
    public static final String PROP_CONTEXT_PATH    = "contextPath";
    public static final String PROP_VHOST           = "vhost";
    public static final String PROP_LAST_NODE       = "lastNode";
    public static final String PROP_EXPIRY          = "expiry";
    public static final String PROP_CREATED         = "created";
    public static final String PROP_COOKIE_SET      = "cookieSet";
    public static final String PROP_ACCESSED        = "accessed";
    public static final String PROP_LAST_ACCESSED   = "lastAccessed";
    public static final String PROP_MAX_INACTIVE_MS = "maxInactiveMs";
    public static final String PROP_ATTRIBUTES      = "attributes";
    public static final String PROP_DIRTY           = "dirty";
    public static final String PROP_LAST_SAVED      = "lastSaved";

    protected JettySesssionModule() {
        super("jetty-session", 0);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(SESSION_DATA)
                .oProperty(PROP_ID, OType.STRING).notNull().oIndex(OClass.INDEX_TYPE.UNIQUE)
                .oProperty(PROP_CONTEXT_PATH, OType.STRING)
                .oProperty(PROP_VHOST, OType.STRING)
                .oProperty(PROP_LAST_NODE, OType.STRING)
                .oProperty(PROP_EXPIRY, OType.LONG).notNull()
                .oProperty(PROP_CREATED, OType.LONG).notNull()
                .oProperty(PROP_COOKIE_SET, OType.LONG).notNull()
                .oProperty(PROP_ACCESSED, OType.LONG).notNull()
                .oProperty(PROP_LAST_ACCESSED, OType.LONG).notNull()
                .oProperty(PROP_MAX_INACTIVE_MS, OType.LONG).notNull()
                .oProperty(PROP_ATTRIBUTES, OType.EMBEDDEDMAP).notNull()
                .oProperty(PROP_DIRTY, OType.BOOLEAN).notNull()
                .oProperty(PROP_LAST_SAVED, OType.LONG).notNull();

        return null;
    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        SessionDataStoreProxy store = OrienteerSessionDataStoreFactory.getInstance().getSessionDataStore();
        store.setSessionDataStore(new OrientDbSessionDataStore());
    }
}
