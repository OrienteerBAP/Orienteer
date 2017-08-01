package org.orienteer.taucharts;

import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.taucharts.component.widget.AbstractTauchartsWidget;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'taucharts' module
 */
public class Module extends AbstractOrienteerModule{

	protected Module() {
		super("taucharts", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		makeSchema(db);
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		makeSchema(db);
		app.mountPages("org.orienteer.taucharts.web");
		app.registerWidgets("org.orienteer.taucharts.component.widget");
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.taucharts.web");
		app.unregisterWidgets("org.orienteer.taucharts.component.widget");
	}
	
	public void makeSchema(ODatabaseDocument db){
		OSchemaHelper helper = OSchemaHelper.bind(db);

		helper.oClass(AbstractTauchartsWidget.TYPE_OCLASS).domain(OClassDomain.SYSTEM)
			.oProperty("name", OType.STRING, 10).markAsDocumentName()
			.oProperty("alias", OType.STRING, 20).oIndex(INDEX_TYPE.UNIQUE);

		helper.oClass(AbstractTauchartsWidget.PLUGINS_OCLASS).domain(OClassDomain.SYSTEM)
			.oProperty("name", OType.STRING, 10).markAsDocumentName()
			.oProperty("alias", OType.STRING, 20).oIndex(INDEX_TYPE.UNIQUE);

		helper.oClass(AbstractTauchartsWidget.WIDGET_OCLASS_NAME, OWidgetsModule.OCLASS_WIDGET).domain(OClassDomain.SYSTEM)
			.oProperty(AbstractTauchartsWidget.QUERY_PROPERTY_NAME, OType.STRING, 100).assignVisualization("textarea")
			.oProperty(AbstractTauchartsWidget.TYPE_PROPERTY_NAME, OType.LINK, 110).linkedClass(AbstractTauchartsWidget.TYPE_OCLASS).assignVisualization("listbox")
			.oProperty(AbstractTauchartsWidget.X_PROPERTY_NAME, OType.EMBEDDEDLIST, 120).linkedType(OType.STRING)
			.oProperty(AbstractTauchartsWidget.X_LABEL_PROPERTY_NAME, OType.STRING, 125)
			.oProperty(AbstractTauchartsWidget.Y_PROPERTY_NAME, OType.EMBEDDEDLIST, 130).linkedType(OType.STRING)
			.oProperty(AbstractTauchartsWidget.Y_LABEL_PROPERTY_NAME, OType.STRING, 135)
			.oProperty(AbstractTauchartsWidget.COLOR_PROPERTY_NAME, OType.STRING, 140)
			.oProperty(AbstractTauchartsWidget.PLUGINS_PROPERTY_NAME, OType.LINKSET, 150).linkedClass("TauchartsPlugin").assignVisualization("listbox")
			.oProperty(AbstractTauchartsWidget.USING_REST_PROPERTY_NAME, OType.BOOLEAN, 160)
			.oProperty(AbstractTauchartsWidget.CONFIG_PROPERTY_NAME, OType.STRING, 170).assignVisualization("textarea");
		
		makeData(db);
	}
	private void makeData(ODatabaseDocument db){
		db.activateOnCurrentThread();
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Scatterplot","scatterplot");
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Line","line");
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Bar","bar");
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Horizontal bar","horizontalBar");
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Scatterplot","scatterplot");
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Horizontal stacked bar","horizontal-stacked-bar");
		makeDataItem(AbstractTauchartsWidget.TYPE_OCLASS,"Stacked area","stacked-area");
		
		makeDataItem(AbstractTauchartsWidget.PLUGINS_OCLASS,"Tooltip","tooltip");
		makeDataItem(AbstractTauchartsWidget.PLUGINS_OCLASS,"Legend","legend");
		makeDataItem(AbstractTauchartsWidget.PLUGINS_OCLASS,"Quick filter","quick-filter");
		makeDataItem(AbstractTauchartsWidget.PLUGINS_OCLASS,"Floating axes","floating-axes");
		makeDataItem(AbstractTauchartsWidget.PLUGINS_OCLASS,"Trendline","trendline");
	} 

	private void makeDataItem(String oClass, String name, String alias){
		try {
			ODocument doc = new ODocument(oClass);
			doc.field("name",name);
			doc.field("alias",alias);
			doc.save();		
		} catch (ORecordDuplicatedException e) {
			// TODO: ignore duplication
		}
	}
}
