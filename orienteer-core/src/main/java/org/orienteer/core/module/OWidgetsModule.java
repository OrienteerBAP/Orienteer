package org.orienteer.core.module;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OClassDomain;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.widget.AbstractCalculatedDocumentsWidget;
import org.orienteer.core.component.widget.AbstractHtmlJsPaneWidget;
import org.orienteer.core.component.widget.document.CalculatedDocumentsWidget;
import org.orienteer.core.component.widget.document.ExternalPageWidget;
import org.orienteer.core.component.widget.document.ExternalViewWidget;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.IWidgetTypesRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * {@link IOrienteerModule} to install widget specific things
 */
@Singleton
public class OWidgetsModule extends AbstractOrienteerModule {
	
	private static final Logger LOG = LoggerFactory.getLogger(OWidgetsModule.class);
	
	public static final String NAME = "widgets";

	public static final String OCLASS_DASHBOARD = "ODashboard";
	public static final String OPROPERTY_DOMAIN = "domain";
	public static final String OPROPERTY_TAB = "tab";
	public static final String OPROPERTY_CLASS = "class";
	public static final String OPROPERTY_LINKED_IDENTITY = "linked";
	public static final String OPROPERTY_WIDGETS = "widgets";
	
	public static final String OCLASS_WIDGET = "OWidget";
	public static final String OPROPERTY_DASHBOARD = "dashboard";
	public static final String OPROPERTY_TYPE_ID = "typeId";
	public static final String OPROPERTY_TITLE = "title";
	public static final String OPROPERTY_COL = "col";
	public static final String OPROPERTY_ROW = "row";
	public static final String OPROPERTY_SIZE_X = "sizeX";
	public static final String OPROPERTY_SIZE_Y = "sizeY";
	public static final String OPROPERTY_HIDDEN = "hidden";
	public static final String OPROPERTY_PAGE_URL = "pageUrl";
	public static final String OPROPERTY_STYLE = "style";
	
	@Inject
	private IWidgetTypesRegistry registry;
	
	public OWidgetsModule() {
		super(NAME, 6);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OCLASS_DASHBOARD)
					.oProperty(OPROPERTY_DOMAIN, OType.STRING, 10).oIndex(INDEX_TYPE.NOTUNIQUE).markDisplayable()
					.oProperty(OPROPERTY_TAB, OType.STRING, 20).oIndex(INDEX_TYPE.NOTUNIQUE).markDisplayable()
					.oProperty(OPROPERTY_LINKED_IDENTITY, OType.LINK, 30).markDisplayable()
					.oProperty(OPROPERTY_CLASS, OType.STRING, 40)
					.oProperty(OPROPERTY_WIDGETS, OType.LINKLIST, 50).assignVisualization("table");
		helper.oClass(OCLASS_WIDGET)
					.oProperty(OPROPERTY_TITLE, OType.EMBEDDEDMAP, 0).assignVisualization("localization")
					.oProperty(OPROPERTY_DASHBOARD, OType.LINK, 10).markDisplayable().markAsLinkToParent()
					.oProperty(OPROPERTY_TYPE_ID, OType.STRING, 20).markDisplayable().markAsDocumentName()
					.oProperty(OPROPERTY_COL, OType.INTEGER, 30)
					.oProperty(OPROPERTY_ROW, OType.INTEGER, 40)
					.oProperty(OPROPERTY_SIZE_X, OType.INTEGER, 50)
					.oProperty(OPROPERTY_SIZE_Y, OType.INTEGER, 60)
					.oProperty(OPROPERTY_HIDDEN, OType.BOOLEAN, 60);
		helper.setupRelationship(OCLASS_DASHBOARD, OPROPERTY_WIDGETS, OCLASS_WIDGET, OPROPERTY_DASHBOARD);
		installWidgetsSchemaV2(db); 
		installWidgetsSchemaV3(db);
		installWidgetsSchemaV4(db);
		installWidgetsSchemaV5(db);
		installWidgetsSchemaV6(db); 
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db,
			int oldVersion, int newVersion) {
		switch(oldVersion) {
			case 2:
				installWidgetsSchemaV2(db);
			case 3:
				installWidgetsSchemaV3(db);
			case 4:
				installWidgetsSchemaV4(db);
			case 5:
				installWidgetsSchemaV5(db);
			case 6:
				installWidgetsSchemaV6(db);
		}
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		List<IWidgetType<?>> notInstalled = checkWidgetClassesInstallation(db);
		if(!notInstalled.isEmpty()) {
			LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			LOG.error("!!! NOT ALL WIDGET CLASSES WERE INSTALLED !!!");
			LOG.error("!!!          TRYING TO REINSTALL          !!!");
			LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			onInstall(app, db);
			LOG.error("Problematic widget types: "+desribeWidgetTypes(notInstalled));
			notInstalled = checkWidgetClassesInstallation(db);
			if(!notInstalled.isEmpty()) {
				LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				LOG.error("!!!   REINSTALL HAS NOT FIXED A PROBLEM   !!!");
				LOG.error("!!!     SYSTEM MIGHT WORK INCORRECTLY     !!!");
				LOG.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				LOG.error("Problematic widget types: "+desribeWidgetTypes(notInstalled));
			}
		}
	}
	
	private List<IWidgetType<?>> checkWidgetClassesInstallation(ODatabaseDocument db) {
		final OSchema schema = db.getMetadata().getSchema();
		return registry.listWidgetTypes(new Predicate<IWidgetType<Object>>() {

			@Override
			public boolean apply(IWidgetType<Object> input) {
				String oClassName = input.getOClassName();
				if(Strings.isEmpty(oClassName)) return false;
				return !schema.existsClass(oClassName);
			}
		});
	}
	
	private String desribeWidgetTypes(List<IWidgetType<?>> widgetTypes) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<IWidgetType<?>> iterator = widgetTypes.iterator(); iterator.hasNext();) {
			IWidgetType<?> iWidgetType = iterator.next();
			sb.append(iWidgetType.getId()).append(':').append(iWidgetType.getOClassName()).append(System.lineSeparator());
		}
		return sb.toString();
	}
	
	protected void installWidgetsSchemaV2(ODatabaseSession db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(AbstractHtmlJsPaneWidget.WIDGET_OCLASS_NAME, OCLASS_WIDGET)
				.oProperty("html", OType.STRING, 10).assignVisualization("html")
				.oProperty("script", OType.STRING, 20).assignVisualization("javascript")
				.oProperty("resources", OType.EMBEDDEDLIST, 30).linkedType(OType.STRING);
		if(!helper.getOClass().existsProperty("title")) helper.oProperty("title", OType.STRING, 0);

		helper.oClass(ExternalPageWidget.WIDGET_OCLASS_NAME, OCLASS_WIDGET)
				.oProperty("pageUrl", OType.STRING, 0)
				.oProperty("style", OType.STRING, 10);

        helper.oClass(CalculatedDocumentsWidget.WIDGET_OCLASS_NAME, OCLASS_WIDGET)
                .oProperty("query", OType.STRING, 0).assignVisualization("textarea");
	}
	
	protected void installWidgetsSchemaV3(ODatabaseSession db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		OClass widgetClass = helper.oClass(OCLASS_WIDGET).getOClass();
		if(!widgetClass.existsProperty(OPROPERTY_TITLE)) {
			db.command("UPDATE "+OCLASS_WIDGET+" REMOVE title");
		}
		OClass classToFix = db.getMetadata().getSchema()
						.getClass(AbstractHtmlJsPaneWidget.WIDGET_OCLASS_NAME);
		if(classToFix!=null && classToFix.existsProperty("title") && !widgetClass.existsProperty("title")) classToFix.dropProperty("title");
		
		helper.oClass(OCLASS_WIDGET)
			.oProperty(OPROPERTY_TITLE, OType.EMBEDDEDMAP, 0).assignVisualization("localization");
	}
	
	protected void installWidgetsSchemaV4(ODatabaseDocument db) {
		for(OClass subClass : db.getMetadata().getSchema().getClass(OCLASS_WIDGET).getSubclasses()) {
			CustomAttribute.DOMAIN.setValue(subClass, OClassDomain.SPECIFICATION);
		}
	}
	
	protected void installWidgetsSchemaV5(ODatabaseSession db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
        helper.oClass(AbstractCalculatedDocumentsWidget.WIDGET_OCLASS_NAME, OCLASS_WIDGET)
                .oProperty("class", OType.STRING, 10).notNull(true);
	}
	
	protected void installWidgetsSchemaV6(ODatabaseSession db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(ExternalViewWidget.EXTERNAL_VIEW_WIDGET_CLASS, OWidgetsModule.OCLASS_WIDGET)
			.oProperty(ExternalViewWidget.TABS_PROPERTY_NAME, OType.EMBEDDEDMAP, 50 ).linkedType(OType.STRING);
	}
}
