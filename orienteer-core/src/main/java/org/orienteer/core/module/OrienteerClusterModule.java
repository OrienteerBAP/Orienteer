package org.orienteer.core.module;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hazelcast.core.IMap;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.wicket.pageStore.OWicketData;

import java.util.Set;

/**
 * Module which create data model for run Orienteer in cluster mode
 */
public class OrienteerClusterModule extends AbstractOrienteerModule {

    @Inject
    @Named("orienteer.sessions.map.name")
    private String mapName;

    public OrienteerClusterModule() {
        super("orienteer-cluster", 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(OWicketData.CLASS_NAME)
                .domain(OClassDomain.SYSTEM)
                .oProperty(OWicketData.PROP_ID, OType.INTEGER, 0).notNull().markAsDocumentName()
                .oProperty(OWicketData.PROP_SESSION_ID, OType.STRING, 10).notNull()
                .oProperty(OWicketData.PROP_DATA, OType.BINARY, 20).notNull();
        return null;
    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
        app.getHazelcast().ifPresent(hazelcast -> {
            IMap<String, Object> sessionMap = hazelcast.getMap(mapName);
            removeOutdatedPages(db, sessionMap.keySet());
        });
    }

    /**
     * Remove pages if they are stored inside db and there is no active sessions which need one of this pages
     * @param db database
     * @param sessionIds active session ids
     */
    private void removeOutdatedPages(ODatabaseSession db, Set<String> sessionIds) {
        db.begin();
        String sql = String.format("delete from %s where not(%s in ?)", OWicketData.CLASS_NAME, OWicketData.PROP_SESSION_ID);
        for (int i = 0; i <= 10; i++) {
            try {
                db.command(sql, sessionIds).close();
                db.commit();
                break;
            } catch (Exception ex) {
                if (i == 10) {
                    throw new IllegalStateException("Can't remove outdated pages!", ex);
                }
            }
        }
    }
}
