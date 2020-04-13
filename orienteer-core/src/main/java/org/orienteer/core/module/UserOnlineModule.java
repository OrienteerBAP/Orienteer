package org.orienteer.core.module;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Module to support user's online/offline lifecycle
 */
@Singleton
public class UserOnlineModule extends AbstractOrienteerModule {

    public static final String NAME = "user-online";

    public static final String PROP_ONLINE             = "online";
    public static final String PROP_LAST_SESSION_FIELD = "lastSessionId";

    public UserOnlineModule() {
        super(NAME, 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        super.onInstall(app, db);
        OSchemaHelper helper = OSchemaHelper.bind(db);

        helper.oClass(OUser.CLASS_NAME)
                .oProperty(PROP_ONLINE, OType.BOOLEAN)
                .oProperty(PROP_LAST_SESSION_FIELD, OType.STRING)
                .switchDisplayable(true, PROP_ONLINE, PROP_LAST_SESSION_FIELD);

        return null;
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        super.onInitialize(app, db);
        resetUsersOnline(db);
        app.getSessionListeners().add(createUserOnlineListener());
    }

    public ODocument updateOnlineUser(final OSecurityUser user, final boolean online) {
        return updateUserFieldAndGetUser(user, PROP_ONLINE, online);
    }

    public void updateSessionUser(final OSecurityUser user, final String sessionId) {
    	updateUserFieldAndGetUser(user, PROP_LAST_SESSION_FIELD, sessionId);
    }

    private ODocument updateUserFieldAndGetUser(OSecurityUser user, String field, Object data) {
        if (user == null) {
            return null;
        }

        return DBClosure.sudo(db -> {
            ODocument document = user.getDocument();
            document = (ODocument) document.reload();
            document.field(field, data);
            document.save();
            return document;
        });
    }

    private ISessionListener createUserOnlineListener() {
        return new ISessionListener() {
            @Override
            public void onCreated(Session session) {}

            @Override
            public void onUnbound(final String sessionId) {
                DBClosure.sudoConsumer(db -> {
                    String sql = String.format("update %s set %s = ? where %s = ?", OUser.CLASS_NAME,
                            PROP_ONLINE, PROP_LAST_SESSION_FIELD);
                    db.command(sql, false, sessionId);
                });
            }
        };
    }

    private void resetUsersOnline(ODatabaseDocument db) {
        String sql = String.format("update %s set %s = ?", OUser.CLASS_NAME, PROP_ONLINE);
        db.command(sql, false);
    }
}
