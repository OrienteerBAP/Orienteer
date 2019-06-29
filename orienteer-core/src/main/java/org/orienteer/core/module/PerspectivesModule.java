package org.orienteer.core.module;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OIdentity;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link IOrienteerModule} for "perspectives" feature of Orienteer
 */
@Singleton
public class PerspectivesModule extends AbstractOrienteerModule {

	public static final String NAME = "perspectives";

	public static final String ALIAS_PERSPECTIVE_DEFAULT = "default";

	public static final String ALIAS_ITEM_USERS        = "users";
	public static final String ALIAS_ITEM_ROLES        = "roles";
	public static final String ALIAS_ITEM_SCHEMA       = "schema";
	public static final String ALIAS_ITEM_LOCALIZATION = "localization";
	public static final String ALIAS_ITEM_PERSPECTIVES = "perspectives";
	public static final String ALIAS_ITEM_MODULES      = "modules";

	public static final String PROP_PERSPECTIVE = "perspective";

	public PerspectivesModule()
	{
		super(NAME, 6);
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);

		helper.oClass(OPerspective.CLASS_NAME)
				.oProperty(OPerspective.PROP_NAME, OType.EMBEDDEDMAP, 0)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
					.markAsDocumentName()
					.linkedType(OType.STRING)
				.oProperty(OPerspective.PROP_ALIAS, OType.STRING, 10)
					.notNull()
					.oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OPerspective.PROP_ICON, OType.STRING, 20)
				.oProperty(OPerspective.PROP_HOME_URL, OType.STRING, 30)
				.oProperty(OPerspective.PROP_MENU, OType.LINKLIST, 40)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_TABLE)
				.oProperty(OPerspective.PROP_FOOTER, OType.STRING, 50)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_TEXTAREA)
				.switchDisplayable(true, OPerspective.PROP_NAME, OPerspective.PROP_ICON, OPerspective.PROP_HOME_URL);

		helper.oClass(OPerspectiveItem.CLASS_NAME)
				.oProperty(OPerspectiveItem.PROP_NAME, OType.EMBEDDEDMAP, 0)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
					.markAsDocumentName()
				.oProperty(OPerspectiveItem.PROP_ALIAS, OType.STRING, 10)
					.notNull()
					.oIndex(INDEX_TYPE.UNIQUE)
				.oProperty(OPerspectiveItem.PROP_ICON, OType.STRING, 20)
				.oProperty(OPerspectiveItem.PROP_URL, OType.STRING, 30)
				.oProperty(OPerspectiveItem.PROP_PERSPECTIVE, OType.LINK, 40)
					.markAsLinkToParent()
				.oProperty(OPerspectiveItem.PROP_PERSPECTIVE_ITEM, OType.LINK, 50)
					.markAsLinkToParent()
				.oProperty(OPerspectiveItem.PROP_SUB_ITEMS, OType.LINKLIST, 60)
					.assignVisualization(UIVisualizersRegistry.VISUALIZER_TABLE)
				.switchDisplayable(true, OPerspectiveItem.PROP_NAME, OPerspectiveItem.PROP_ICON, OPerspectiveItem.PROP_URL);

		helper.setupRelationship(OPerspective.CLASS_NAME, OPerspective.PROP_MENU, OPerspectiveItem.CLASS_NAME, OPerspectiveItem.PROP_PERSPECTIVE);
		helper.setupRelationship(OPerspectiveItem.CLASS_NAME, OPerspectiveItem.PROP_SUB_ITEMS, OPerspectiveItem.CLASS_NAME, OPerspectiveItem.PROP_PERSPECTIVE_ITEM);

		helper.oClass(OIdentity.CLASS_NAME)
				.oProperty(PROP_PERSPECTIVE, OType.LINK)
					.linkedClass(OPerspective.CLASS_NAME);

		createDefaultPerspective(helper);

		return null;
	}

	private void createDefaultPerspective(OSchemaHelper helper) {

		helper.oClass(OPerspective.CLASS_NAME);

		ODocument defaultPerspective = helper.oDocument(OPerspective.PROP_ALIAS, ALIAS_PERSPECTIVE_DEFAULT)
				.field(OPerspective.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.default.name").getObject()))
				.field(OPerspective.PROP_HOME_URL, "/classes")
				.field(OPerspective.PROP_ICON, "fa fa-cog")
				.saveDocument()
				.getODocument();

		helper.oClass(OPerspectiveItem.CLASS_NAME);

		helper.oDocument(OPerspectiveItem.PROP_ALIAS, ALIAS_ITEM_USERS)
				.field(OPerspectiveItem.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.item.default.users").getObject()))
				.field(OPerspectiveItem.PROP_ICON, "fa fa-users")
				.field(OPerspectiveItem.PROP_URL, "/browse/OUser")
				.field(OPerspectiveItem.PROP_PERSPECTIVE, defaultPerspective)
				.saveDocument();


		helper.oDocument(OPerspectiveItem.PROP_ALIAS, ALIAS_ITEM_ROLES)
				.field(OPerspectiveItem.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.item.default.roles").getObject()))
				.field(OPerspectiveItem.PROP_ICON, "fa fa-user-circle")
				.field(OPerspectiveItem.PROP_URL, "/browse/ORole")
				.field(OPerspectiveItem.PROP_PERSPECTIVE, defaultPerspective)
				.saveDocument();

		helper.oDocument(OPerspectiveItem.PROP_ALIAS, ALIAS_ITEM_SCHEMA)
				.field(OPerspectiveItem.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.item.default.schema").getObject()))
				.field(OPerspectiveItem.PROP_ICON, "fa fa-cubes")
				.field(OPerspectiveItem.PROP_URL, "/schema")
				.field(OPerspectiveItem.PROP_PERSPECTIVE, defaultPerspective)
				.saveDocument();

		helper.oDocument(OPerspectiveItem.PROP_ALIAS, ALIAS_ITEM_LOCALIZATION)
				.field(OPerspectiveItem.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.item.default.localization").getObject()))
				.field(OPerspectiveItem.PROP_ICON, "fa fa-language")
				.field(OPerspectiveItem.PROP_URL, "/browse/OLocalization")
				.field(OPerspectiveItem.PROP_PERSPECTIVE, defaultPerspective)
				.saveDocument();

		helper.oDocument(OPerspectiveItem.PROP_ALIAS, ALIAS_ITEM_PERSPECTIVES)
				.field(OPerspectiveItem.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.item.default.perspectives").getObject()))
				.field(OPerspectiveItem.PROP_ICON, "fa fa-desktop")
				.field(OPerspectiveItem.PROP_URL, "/browse/" + OPerspective.CLASS_NAME)
				.field(OPerspectiveItem.PROP_PERSPECTIVE, defaultPerspective)
				.saveDocument();

		helper.oDocument(OPerspectiveItem.PROP_ALIAS, ALIAS_ITEM_MODULES)
				.field(OPerspectiveItem.PROP_NAME, CommonUtils.toMap("en", new ResourceModel("perspective.item.default.modules").getObject()))
				.field(OPerspectiveItem.PROP_ICON, "fa fa-archive")
				.field(OPerspectiveItem.PROP_URL, "/browse/" + AbstractOrienteerModule.OMODULE_CLASS)
				.field(OPerspectiveItem.PROP_PERSPECTIVE, defaultPerspective)
				.saveDocument();
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		int toVersion = oldVersion+1;
		switch (toVersion) {
			case 2:
				convertNameProperty(app, db, OPerspective.CLASS_NAME);
				convertNameProperty(app, db, OPerspectiveItem.CLASS_NAME);
				break;
			case 3:
			case 6:
				onInstall(app, db);
				break;
			case 4:
				OIndex<?> index = db.getMetadata().getIndexManager().getIndex(OPerspective.CLASS_NAME + ".name");
				if(index!=null) index.delete();
				onInstall(app, db);
				break;
			case 5:
				OSchemaHelper.bind(db)
					.oClass(OIdentity.CLASS_NAME)
					.oProperty(PROP_PERSPECTIVE, OType.LINK).linkedClass(OPerspective.CLASS_NAME);
				break;
			default:
				break;
		}
		if(toVersion<newVersion) onUpdate(app, db, toVersion, newVersion);
	}
	
	private void convertNameProperty(OrienteerWebApplication app, ODatabaseDocument db, String className) {
		boolean wasInTransacton = db.getTransaction().isActive();
		db.commit();
		for(ODocument doc : db.browseClass(className)) {
			Object value = doc.field("name");
			if(value instanceof String) {
				doc.field("temp", (Object) doc.field("name"));
				doc.field("name", (String) null);
				doc.save();
			}
		}
		OClass oClass = db.getMetadata().getSchema().getClass(className);
		oClass.dropProperty("name");
		OProperty nameProperty = oClass.createProperty("name", OType.EMBEDDEDMAP);
		CustomAttribute.VISUALIZATION_TYPE.setValue(nameProperty, "localization");
		for(ODocument doc : db.browseClass(className)) {
			if(doc.containsField("temp")) {
				doc.field("name", CommonUtils.toMap("en", doc.field("temp")));
				doc.removeField("temp");
				doc.save();
			}
		}
		if(wasInTransacton) db.begin();
	}


	public Optional<OPerspective> getPerspectiveByAlias(ODatabaseDocument db, String alias) {
		return getPerspectiveByAliasAsDocument(db, alias)
				.map(OPerspective::new);
	}

	public Optional<ODocument> getPerspectiveByAliasAsDocument(ODatabaseDocument db, String alias) {
		String sql = String.format("select from %s where %s = ?", OPerspective.CLASS_NAME, OPerspective.PROP_ALIAS);
		List<OIdentifiable> identifiables = db.query(new OSQLSynchQuery<>(sql, 1), alias);
		return CommonUtils.getDocument(identifiables);
	}

	public Optional<OPerspectiveItem> getPerspectiveItemByAlias(ODatabaseDocument db, String alias) {
	    return getPerspectiveItemByAliasAsDocument(db, alias)
                .map(OPerspectiveItem::new);
    }

	public Optional<ODocument> getPerspectiveItemByAliasAsDocument(ODatabaseDocument db, String alias) {
	    String sql = String.format("select from %s where %s =?", OPerspectiveItem.CLASS_NAME, OPerspectiveItem.PROP_ALIAS);
	    List<OIdentifiable> identifiable = db.query(new OSQLSynchQuery<>(sql, 1), alias);
	    return CommonUtils.getDocument(identifiable);
    }
	
	public ODocument getDefaultPerspective(ODatabaseDocument db, OSecurityUser user) {
		if (user != null) {
			if (user.getDocument().field(PROP_PERSPECTIVE) != null) {
				return ((OIdentifiable) user.getDocument().field(PROP_PERSPECTIVE)).getRecord();
			}

			Set<? extends OSecurityRole> roles = user.getRoles();
			for (OSecurityRole oRole : roles) {
				ODocument perspective = getPerspectiveForORole(oRole);
				if (perspective != null) {
					return perspective;
				}
			}
		}

		return getPerspectiveByAliasAsDocument(db, ALIAS_PERSPECTIVE_DEFAULT)
				.orElse(null);
	}
	
	public ODocument getPerspectiveForORole(OSecurityRole role) {
		if (role == null) {
			return null;
		}

		if (role.getDocument().field(PROP_PERSPECTIVE) != null) {
			return ((OIdentifiable) role.getDocument().field(PROP_PERSPECTIVE)).getRecord();
		}

		OSecurityRole parentRole = role.getParentRole();
		return parentRole != null && !parentRole.equals(role) ? getPerspectiveForORole(role) : null;
	}

	public void updateUserPerspective(ODocument user, ODocument perspective) {
		if (user != null) {
			DBClosure.sudoConsumer(db -> {
				user.field(PROP_PERSPECTIVE, perspective);
				user.save();
			});
		}
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema = db.getMetadata().getSchema();
		if (schema.getClass(OPerspective.CLASS_NAME) == null || schema.getClass(OPerspectiveItem.CLASS_NAME) == null) {
			//Repair
			onInstall(app, db);
		}
	}

	/**
	 * Model which represents class OPerspective
	 */
	public static class OPerspective extends ODocumentWrapper {

		public static final String CLASS_NAME = "OPerspective";

		public static final String PROP_NAME     = "name";
		public static final String PROP_ALIAS    = "alias";
		public static final String PROP_ICON     = "icon";
		public static final String PROP_HOME_URL = "homeUrl";
		public static final String PROP_MENU     = "menu";
		public static final String PROP_FOOTER   = "footer";

		public OPerspective() {
			super(CLASS_NAME);
		}

		public OPerspective(String iClassName) {
			super(iClassName);
		}

		public OPerspective(ODocument iDocument) {
			super(iDocument);
		}

		public Map<String, String> getName() {
			return document.field(PROP_NAME);
		}

		public OPerspective setName(Map<String, String> name) {
			document.field(PROP_NAME, name);
			return this;
		}

		public String getAlias() {
			return document.field(PROP_ALIAS);
		}

		public OPerspective setAlias(String alias) {
			document.field(PROP_ALIAS, alias);
			return this;
		}

		public String getIcon() {
			return document.field(PROP_ICON);
		}

		public OPerspective setIcon(String icon) {
			document.field(PROP_ICON, icon);
			return this;
		}

		public String getHomeUrl() {
			return document.field(PROP_HOME_URL);
		}

		public OPerspective setHomeUrl(String url) {
			document.field(PROP_HOME_URL, url);
			return this;
		}

		public List<OPerspectiveItem> getMenu() {
			return getMenuAsDocuments().stream()
					.map(OPerspectiveItem::new)
					.collect(Collectors.toCollection(LinkedList::new));
		}

		public List<ODocument> getMenuAsDocuments() {
			return CommonUtils.getDocuments(document.field(PROP_MENU));
		}

		public OPerspective setMenu(List<OPerspectiveItem> menu) {
			List<ODocument> docs = menu == null ? Collections.emptyList() : menu.stream()
					.map(OPerspectiveItem::getDocument)
					.collect(Collectors.toCollection(LinkedList::new));
			return setMenuAsDocuments(docs);
		}

		public OPerspective setMenuAsDocuments(List<ODocument> menu) {
			document.field(PROP_MENU, menu);
			return this;
		}

		public String getFooter() {
			return document.field(PROP_FOOTER);
		}

		public OPerspective setFooter(String footer) {
			document.field(PROP_FOOTER, footer);
			return this;
		}
	}

	/**
	 * Model which represents class OPerspectiveItem
	 */
	public static class OPerspectiveItem extends ODocumentWrapper {

		public static final String CLASS_NAME = "OPerspectiveItem";

		public static final String PROP_NAME             = "name";
		public static final String PROP_ALIAS            = "alias";
		public static final String PROP_ICON             = "icon";
		public static final String PROP_URL              = "url";
		public static final String PROP_PERSPECTIVE      = "perspective";
		public static final String PROP_PERSPECTIVE_ITEM = "perspectiveItem";
		public static final String PROP_SUB_ITEMS        = "subItems";

		public OPerspectiveItem() {
			super(CLASS_NAME);
		}

		public OPerspectiveItem(String iClassName) {
			super(iClassName);
		}

		public OPerspectiveItem(ODocument iDocument) {
			super(iDocument);
		}

		public Map<String, String> getName() {
			return document.field(PROP_NAME);
		}

		public OPerspectiveItem setName(Map<String, String> name) {
			document.field(PROP_NAME);
			return this;
		}

		public String getAlias() {
			return document.field(PROP_ALIAS);
		}

		public OPerspectiveItem setAlias(String alias) {
			document.field(PROP_ALIAS);
			return this;
		}

		public String getIcon() {
			return document.field(PROP_ICON);
		}

		public OPerspectiveItem setIcon(String icon) {
			document.field(PROP_ICON, icon);
			return this;
		}

		public String getUrl() {
			return document.field(PROP_URL);
		}

		public OPerspectiveItem setUrl(String url) {
			document.field(PROP_URL, url);
			return this;
		}

		public OPerspective getPerspective() {
			ODocument perspective = getPerspectiveAsDocument();
			return perspective != null ? new OPerspective(perspective) : null;
		}

		public ODocument getPerspectiveAsDocument() {
			OIdentifiable perspective = document.field(PROP_PERSPECTIVE);
			return perspective != null ? perspective.getRecord() : null;
		}

		public OPerspectiveItem setPerspective(OPerspective perspective) {
			return setPerspectiveAsDocument(perspective != null ? perspective.getDocument() : null);
		}

		public OPerspectiveItem setPerspectiveAsDocument(ODocument perspective) {
			document.field(PROP_PERSPECTIVE, perspective);
			return this;
		}

		public OPerspectiveItem getPerspectiveItem() {
			ODocument perspectiveItem = getPerspectiveItemAsDocument();
			return perspectiveItem != null ? new OPerspectiveItem(perspectiveItem) : null;
		}

		public ODocument getPerspectiveItemAsDocument() {
			OIdentifiable perspectiveItem = document.field(PROP_PERSPECTIVE_ITEM);
			return perspectiveItem != null ? perspectiveItem.getRecord() : null;
		}

		public OPerspectiveItem setPerspectiveItem(OPerspectiveItem perspectiveItem) {
			return setPerspectiveItemAsDocument(perspectiveItem != null ? perspectiveItem.getDocument() : null);
		}

		public OPerspectiveItem setPerspectiveItemAsDocument(ODocument perspectiveItem) {
			document.field(PROP_PERSPECTIVE_ITEM, perspectiveItem);
			return this;
		}

		public List<OPerspectiveItem> getSubItems() {
			return getSubItemsAsDocuments().stream()
					.map(OPerspectiveItem::new)
					.collect(Collectors.toCollection(LinkedList::new));
		}

		public List<ODocument> getSubItemsAsDocuments() {
			List<OIdentifiable> subItems = document.field(PROP_SUB_ITEMS);
			return CommonUtils.getDocuments(subItems);
		}

		public OPerspectiveItem setSubItems(List<OPerspectiveItem> subItems) {
			List<ODocument> docs = subItems == null ? Collections.emptyList() : subItems.stream()
					.map(OPerspectiveItem::getDocument)
					.collect(Collectors.toCollection(LinkedList::new));
			return setSubItemsAsDocuments(docs);
		}

		public OPerspectiveItem setSubItemsAsDocuments(List<ODocument> subItems) {
			document.field(PROP_SUB_ITEMS, subItems);
			return this;
		}
	}

}
