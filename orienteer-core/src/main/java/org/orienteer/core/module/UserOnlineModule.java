package org.orienteer.core.module;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * @author Kirill Mukhov
 */
@Singleton
public class UserOnlineModule extends AbstractOrienteerModule {

    public static final String NAME = "user-online";
    public static final String OCLASS_USER = "OUser";

    public static final String ONLINE_FIELD = "online";
    public static final String LAST_SESSION_FIELD = "lastSessionId";

    public UserOnlineModule() {
        super(NAME, 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        super.onInstall(app, db);
        OSchemaHelper helper = OSchemaHelper.bind(db);

        helper.oClass(OCLASS_USER)
                .oProperty(ONLINE_FIELD, OType.BOOLEAN)
                .oProperty(LAST_SESSION_FIELD, OType.STRING)
                .switchDisplayable(true, ONLINE_FIELD, LAST_SESSION_FIELD);

        return null;
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        super.onInitialize(app, db);
        app.getSessionListeners().add(new ISessionListener() {
            @Override
            public void onCreated(Session session) { }

            @Override
            public void onUnbound(final String sessionId) {
                new DBClosure<Void>() {
                    @Override
                    protected Void execute(ODatabaseDocument db) {
                        db.command(new OSQLSynchQuery<Void>("UPDATE " + OCLASS_USER + " set " +
                                ONLINE_FIELD + "=false where " + LAST_SESSION_FIELD + "=\""+sessionId + "\""));
                        return null;
                    }
                }.execute();
            }
        });
    }

    public ODocument updateOnlineUser(OUser user, final boolean online) {
        final ODocument document = user.getDocument();
        DBClosure<ODocument> closure = new DBClosure<ODocument>() {
            @Override
            protected ODocument execute(ODatabaseDocument oDatabaseDocument) {
                document.field(ONLINE_FIELD, online);
                document.save();
                return document;
            }
        };
        return closure.execute();
    }

    public void updateSessionUser(OUser user, final String sessionId) {
        final ODocument document = user.getDocument();
        DBClosure<ODocument> closure = new DBClosure<ODocument>() {
            @Override
            protected ODocument execute(ODatabaseDocument oDatabaseDocument) {
                document.field(LAST_SESSION_FIELD, sessionId);
                document.save();
                return document;
            }
        };
        closure.execute();
    }
}
