package org.orienteer.core.module;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.widget.document.ODocumentPropertiesWidget;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.widget.IWidgetTypesRegistry;
import org.orienteer.core.widget.TestWidget;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} to install widget specific things
 */
@Singleton
public class OWidgetsModule extends AbstractOrienteerModule {

	public static final String OCLASS_DASHBOARD = "ODashboard";
	public static final String OPROPERTY_DOMAIN = "domain";
	public static final String OPROPERTY_TAB = "tab";
	public static final String OPROPERTY_LINKED_IDENTITY = "linked";
	public static final String OPROPERTY_WIDGETS = "widgets";
	
	public static final String OCLASS_WIDGET = "OWidget";
	public static final String OPROPERTY_DASHBOARD = "dashboard";
	public static final String OPROPERTY_TYPE_ID = "typeId";
	public static final String OPROPERTY_COL = "col";
	public static final String OPROPERTY_ROW = "row";
	public static final String OPROPERTY_SIZE_X = "sizeX";
	public static final String OPROPERTY_SIZE_Y = "sizeY";
	public static final String OPROPERTY_HIDDEN = "hidden";
	
	public OWidgetsModule() {
		super("widgets", 1);
	}
	
	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OCLASS_DASHBOARD)
					.oProperty(OPROPERTY_DOMAIN, OType.STRING, 10).oIndex(INDEX_TYPE.NOTUNIQUE).markDisplayable()
					.oProperty(OPROPERTY_TAB, OType.STRING, 20).oIndex(INDEX_TYPE.NOTUNIQUE).markDisplayable()
					.oProperty(OPROPERTY_LINKED_IDENTITY, OType.LINK, 30).markDisplayable()
					.oProperty(OPROPERTY_WIDGETS, OType.LINKLIST, 40).assignVisualization("table");
		helper.oClass(OCLASS_WIDGET)
					.oProperty(OPROPERTY_DASHBOARD, OType.LINK, 10).markDisplayable().markAsLinkToParent()
					.oProperty(OPROPERTY_TYPE_ID, OType.STRING, 20).markDisplayable().markAsDocumentName()
					.oProperty(OPROPERTY_COL, OType.INTEGER, 30)
					.oProperty(OPROPERTY_ROW, OType.INTEGER, 40)
					.oProperty(OPROPERTY_SIZE_X, OType.INTEGER, 50)
					.oProperty(OPROPERTY_SIZE_Y, OType.INTEGER, 60)
					.oProperty(OPROPERTY_HIDDEN, OType.BOOLEAN, 60);
		helper.setupRelationship(OCLASS_DASHBOARD, OPROPERTY_WIDGETS, OCLASS_WIDGET, OPROPERTY_DASHBOARD);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		IWidgetTypesRegistry registry = app.getServiceInstance(IWidgetTypesRegistry.class);
		registry.register(TestWidget.class);
		registry.register(ODocumentPropertiesWidget.class);
	}

}
