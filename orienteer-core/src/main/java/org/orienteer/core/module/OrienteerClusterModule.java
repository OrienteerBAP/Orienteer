package org.orienteer.core.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.jetty.OrientDbJettyModule;
import ru.ydn.wicket.wicketorientdb.pageStore.OWicketData;

/**
 * Module which create data model for run Orienteer in cluster mode
 */
public class OrienteerClusterModule extends AbstractOrienteerModule {

    public OrienteerClusterModule() {
        super("orienteer-cluster", 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(OWicketData.CLASS_NAME)
                .domain(OClassDomain.SYSTEM)
                .oProperty(OWicketData.PROP_ID, OType.INTEGER, 0).notNull().markAsDocumentName()
                .oProperty(OWicketData.PROP_SESSION_ID, OType.STRING, 10).notNull()
                .oProperty(OWicketData.PROP_DATA, OType.BINARY, 20).notNull();
        OrientDbJettyModule.initSchema(db);
        return null;
    }

    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
        onInstall(app, db);
    }
}
