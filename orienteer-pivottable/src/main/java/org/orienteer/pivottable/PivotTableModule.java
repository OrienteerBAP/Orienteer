package org.orienteer.pivottable;

import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IOrienteerModule} for 'orienteer-pivottable' module
 */
public class PivotTableModule extends AbstractOrienteerModule{

	public static final String NAME = "pivottable";
	public static final String WIDGET_OCLASS_NAME = "PivotTableWidget";
	public static final String OPROPERTY_PIVOT_TABLE_CONFIG = "pivotTableConfiguration";
	public static final String OPROPERTY_PIVOT_CUSTOM_SQL = "sql";
	
	protected PivotTableModule() {
		super(NAME, 2, OWidgetsModule.NAME);
		
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(WIDGET_OCLASS_NAME, OWidgetsModule.OCLASS_WIDGET)
				.oProperty(OPROPERTY_PIVOT_TABLE_CONFIG, OType.STRING, 100).assignVisualization("textarea")
				.oProperty(OPROPERTY_PIVOT_CUSTOM_SQL, OType.STRING, 110).assignVisualization("textarea");
		return null;
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		onInstall(app, db);
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		app.registerWidgets("org.orienteer.pivottable.component.widget");
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unregisterWidgets("org.orienteer.pivottable.component.widget");
	}
	
}
