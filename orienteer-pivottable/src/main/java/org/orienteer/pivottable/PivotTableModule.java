package org.orienteer.pivottable;

import org.orienteer.core.CustomAttributes;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-pivottable' module
 */
public class PivotTableModule extends AbstractOrienteerModule{

	public static final String WIDGET_OCLASS_NAME = "PivotTableWidget";
	public static final String OPROPERTY_PIVOT_TABLE_CONFIG = "pivotTableConfiguration";
	
	protected PivotTableModule() {
		super("pivottable", 1);
		
	}
	
	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		onUpdate(app, db, 0, getVersion());
	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		if(oldVersion>=newVersion) return;
		switch (oldVersion+1)
		{
			case 1:
				onUpdateToFirstVesion(app, db);
				break;
			default:
				break;
		}
		if(oldVersion+1<newVersion) onUpdate(app, db, oldVersion + 1, newVersion);
	}

	public void onUpdateToFirstVesion(OrienteerWebApplication app, ODatabaseDocument db)
	{
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(WIDGET_OCLASS_NAME, OWidgetsModule.OCLASS_WIDGET)
				.oProperty(OPROPERTY_PIVOT_TABLE_CONFIG, OType.STRING, 100).assignVisualization("textarea");
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		OrienteerWebApplication.get().registerWidgets("org.orienteer.pivottable.component.widget");
	}
	
}
