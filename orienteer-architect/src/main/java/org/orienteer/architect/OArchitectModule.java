package org.orienteer.architect;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.util.OSchemaHelper;

/**
 * 'orienteer-architect' module.
 * Allows edit OrientDB scheme with graphics editor.
 */
public class OArchitectModule extends AbstractOrienteerModule {

    public static final String OARCHITECTOR_CLASS = "OArchitect";
    public static final String NAME               = "name";
    public static final String CONFIG             = "config";

    protected OArchitectModule() {
        super("orienteer-architect", 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(OARCHITECTOR_CLASS, OWidgetsModule.OCLASS_WIDGET)
                .oProperty(NAME, OType.STRING).markAsDocumentName().notNull()
                .oProperty(CONFIG, OType.STRING).switchDisplayable(false);
        return null;
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        app.registerWidgets("org.orienteer.architect.component.widget");
    }

    @Override
    public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {

    }

    @Override
    public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {

    }
}
