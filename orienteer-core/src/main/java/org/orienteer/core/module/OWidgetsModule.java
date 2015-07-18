package org.orienteer.core.module;

import java.util.List;

import org.apache.wicket.util.string.Strings;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.widget.AbstractHtmlJsPaneWidget;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.IWidgetTypesRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} to install widget specific things
 */
@Singleton
public class OWidgetsModule extends AbstractOrienteerModule {
	
	private static final Logger LOG = LoggerFactory.getLogger(OWidgetsModule.class);

	public static final String OCLASS_DASHBOARD = "ODashboard";
	public static final String OPROPERTY_DOMAIN = "domain";
	public static final String OPROPERTY_TAB = "tab";
	public static final String OPROPERTY_CLASS = "class";
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
	
	@Inject
	private IWidgetTypesRegistry registry;
	
	public OWidgetsModule() {
		super("widgets", 2);
	}
	
	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OCLASS_DASHBOARD)
					.oProperty(OPROPERTY_DOMAIN, OType.STRING, 10).oIndex(INDEX_TYPE.NOTUNIQUE).markDisplayable()
					.oProperty(OPROPERTY_TAB, OType.STRING, 20).oIndex(INDEX_TYPE.NOTUNIQUE).markDisplayable()
					.oProperty(OPROPERTY_LINKED_IDENTITY, OType.LINK, 30).markDisplayable()
					.oProperty(OPROPERTY_CLASS, OType.STRING, 40)
					.oProperty(OPROPERTY_WIDGETS, OType.LINKLIST, 50).assignVisualization("table");
		helper.oClass(OCLASS_WIDGET)
					.oProperty(OPROPERTY_DASHBOARD, OType.LINK, 10).markDisplayable().markAsLinkToParent()
					.oProperty(OPROPERTY_TYPE_ID, OType.STRING, 20).markDisplayable().markAsDocumentName()
					.oProperty(OPROPERTY_COL, OType.INTEGER, 30)
					.oProperty(OPROPERTY_ROW, OType.INTEGER, 40)
					.oProperty(OPROPERTY_SIZE_X, OType.INTEGER, 50)
					.oProperty(OPROPERTY_SIZE_Y, OType.INTEGER, 60)
					.oProperty(OPROPERTY_HIDDEN, OType.BOOLEAN, 60);
		helper.setupRelationship(OCLASS_DASHBOARD, OPROPERTY_WIDGETS, OCLASS_WIDGET, OPROPERTY_DASHBOARD);
		installHtmlJsPaneSchema(db); 
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		int updateTo = oldVersion+1;
		switch(updateTo) {
			case 2:
			installHtmlJsPaneSchema(db);
		}
		if(updateTo<newVersion) onUpdate(app, db, updateTo, newVersion);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		if(!checkWidgetClassesInstallation(db)) {
			LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			LOG.error("!!! NOT ALL WIDGET CLASSES WERE INSTALLED !!!");
			LOG.error("!!!          TRYING TO REINSTALL          !!!");
			LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			onInstall(app, db);
			if(!checkWidgetClassesInstallation(db)) {
				LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				LOG.error("!!!   REINSTALL HAS NOT FIXED A PROBLEM   !!!");
				LOG.error("!!!     SYSTEM MIGHT WORK INCORRECTLY     !!!");
				LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}
	}
	
	private boolean checkWidgetClassesInstallation(ODatabaseDocument db) {
		final OSchema schema = db.getMetadata().getSchema();
		List<IWidgetType<?>> notInstalled = registry.listWidgetTypes(new Predicate<IWidgetType<Object>>() {

			@Override
			public boolean apply(IWidgetType<Object> input) {
				String oClassName = input.getOClassName();
				if(Strings.isEmpty(oClassName)) return false;
				return !schema.existsClass(oClassName);
			}
		});
		return notInstalled==null || notInstalled.isEmpty();
	}
	
	protected void installHtmlJsPaneSchema(ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(AbstractHtmlJsPaneWidget.WIDGET_OCLASS_NAME, OCLASS_WIDGET)
				.oProperty("title", OType.STRING, 0)
				.oProperty("html", OType.STRING, 10).assignVisualization("textarea")
				.oProperty("script", OType.STRING, 20).assignVisualization("textarea")
				.oProperty("resources", OType.EMBEDDEDLIST, 30).linkedType(OType.STRING);
				
	}
	
}
