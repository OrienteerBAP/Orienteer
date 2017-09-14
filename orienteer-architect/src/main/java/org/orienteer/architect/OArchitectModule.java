package org.orienteer.architect;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;

/**
 * 'orienteer-architect' module.
 * Allows edit OrientDB scheme with graphics editor.
 */
public class OArchitectModule extends AbstractOrienteerModule {

    public static final String ODATA_MODEL_OCLASS    = "ODataModel";
    public static final String NAME_OPROPERTY        = "name";
    public static final String DESCRIPTION_OPROPERTY = "description";
    public static final String CONFIG_OPROPERTY      = "config";

    protected OArchitectModule() {
        super("architect", 1);
    }

    @Override
    public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
        OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(ODATA_MODEL_OCLASS)
                .oProperty(NAME_OPROPERTY, OType.STRING, 10)
                	.markAsDocumentName()
                	.markDisplayable()
                	.notNull()
            	.oProperty(DESCRIPTION_OPROPERTY, OType.STRING, 20)
                	.markDisplayable()
                	.assignVisualization("textarea")
                .oProperty(CONFIG_OPROPERTY, OType.STRING, 30)
                	.updateCustomAttribute(CustomAttribute.HIDDEN, true)
                	.switchDisplayable(false);
        return null;
    }
    
    @Override
    public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
    	onInstall(app, db);
    }

    @Override
    public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
        app.registerWidgets("org.orienteer.architect.component.widget");
    }

    @Override
    public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
        app.unregisterWidgets("org.orienteer.architect.component.widget");
    }

}
