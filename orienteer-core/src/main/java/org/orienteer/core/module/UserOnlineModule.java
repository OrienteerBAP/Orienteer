package org.orienteer.core.module;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 * Module to support user's online/offline lifecycle
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
                        db.command(new OCommandSQL("UPDATE " + OCLASS_USER + " set " +
                                ONLINE_FIELD + "=false where " + LAST_SESSION_FIELD + "= ?")).execute(sessionId);
                        return null;
                    }
                }.execute();
            }
        });
    }

    public ODocument updateOnlineUser(final OUser user, final boolean online) {
        return new DBClosure<ODocument>() {
            @Override
            protected ODocument execute(ODatabaseDocument oDatabaseDocument) {
            	final ODocument document = user.reload().getDocument();
                document.field(ONLINE_FIELD, online);
                document.save();
                return document;
            }
        }.execute();
    }

    public void updateSessionUser(final OUser user, final String sessionId) {
    	if(user!=null) { 
	        new DBClosure<ODocument>() {
	            @Override
	            protected ODocument execute(ODatabaseDocument oDatabaseDocument) {
	            	final ODocument document = user.reload().getDocument();
	                document.field(LAST_SESSION_FIELD, sessionId);
	                document.save();
	                return document;
	            }
	        }.execute();
    	}
    }
}
