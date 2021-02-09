package org.orienteer.core.module;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
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
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import lombok.experimental.ExtensionMethod;

import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOFieldIndex;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.IODocumentWrapper;
import org.orienteer.core.dao.Lookup;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.module.OrienteerLocalizationModule.IOLocalization;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;
import ru.ydn.wicket.wicketorientdb.utils.LombokExtensions;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link IOrienteerModule} for "perspectives" feature of Orienteer
 */
@Singleton
@ExtensionMethod({LombokExtensions.class})
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
		super(NAME, 7);
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);

		DAO.describe(helper, IOPerspective.class, IOPerspectiveItem.class);

		helper.oClass(OIdentity.CLASS_NAME)
				.oProperty(PROP_PERSPECTIVE, OType.LINK)
					.linkedClass(IOPerspective.CLASS_NAME);

		createDefaultPerspective();

		return null;
	}

	private IOPerspective createDefaultPerspective() {
		
		IOPerspective defaultPerspective = IOPerspective.getOrCreateByAlias(ALIAS_PERSPECTIVE_DEFAULT,
																			"perspective.default.name", 
																			"fa fa-cog", 
																			"/schema");
		
		defaultPerspective.getOrCreatePerspectiveItem(ALIAS_ITEM_USERS, "perspective.item.default.users", "fa fa-users", "/browse/OUser");
		defaultPerspective.getOrCreatePerspectiveItem(ALIAS_ITEM_ROLES, "perspective.item.default.roles", "fa fa-user-circle", "/browse/ORole");
		defaultPerspective.getOrCreatePerspectiveItem(ALIAS_ITEM_SCHEMA, "perspective.item.default.schema", "fa fa-cubes", "/schema");
		defaultPerspective.getOrCreatePerspectiveItem(ALIAS_ITEM_LOCALIZATION, "perspective.item.default.localization", "fa fa-language", "/browse/OLocalization");
		defaultPerspective.getOrCreatePerspectiveItem(ALIAS_ITEM_PERSPECTIVES, "perspective.item.default.perspectives", "fa fa-desktop", "/browse/" + IOPerspective.CLASS_NAME);
		defaultPerspective.getOrCreatePerspectiveItem(ALIAS_ITEM_MODULES, "perspective.item.default.modules", "fa fa-archive", "/browse/" + AbstractOrienteerModule.OMODULE_CLASS);
		return defaultPerspective;
	}
	
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db,
			int oldVersion, int newVersion) {
		int toVersion = oldVersion+1;
		switch (toVersion) {
			case 2:
				convertNameProperty(app, db, IOPerspective.CLASS_NAME);
				convertNameProperty(app, db, IOPerspectiveItem.CLASS_NAME);
				break;
			case 3:
				onInstall(app, db);
				break;
			case 4:
				OIndex index = db.getMetadata().getIndexManager().getIndex(IOPerspective.CLASS_NAME + ".name");
				if(index!=null) index.delete();
				onInstall(app, db);
				break;
			case 5:
				OSchemaHelper.bind(db)
					.oClass(OIdentity.CLASS_NAME)
					.oProperty(PROP_PERSPECTIVE, OType.LINK).linkedClass(IOPerspective.CLASS_NAME);
				break;
			case 6:
				OSchemaHelper helper = OSchemaHelper.bind(db);
				
				helper.oClass(IOPerspective.CLASS_NAME)
					.oProperty("alias", OType.STRING, 10);
				db.command("update OPerspective set alias=name['en'].toLowerCase() where alias is null");
				helper.notNull()
					.oIndex(INDEX_TYPE.UNIQUE);
				//update aliases
				helper.oClass(IOPerspectiveItem.CLASS_NAME)
					.oProperty("alias", OType.STRING, 10);
				db.command("update OPerspectiveItem set alias=name['en'].toLowerCase() where alias is null");
				helper.notNull();
				break;
			case 7:
				OSchemaHelper.bind(db)
					.oClass(IOPerspective.CLASS_NAME)
						.oProperty("features", OType.EMBEDDEDSET, 60)
						.linkedType(OType.STRING);
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


	public Optional<IOPerspective> getPerspectiveByAlias(String alias) {
		return Optional.ofNullable(DAO.create(IOPerspective.class).lookupByAlias(alias));
	}

	public Optional<ODocument> getPerspectiveByAliasAsDocument(String alias) {
		return getPerspectiveByAlias(alias).map(IOPerspective::getDocument);
	}

	public Optional<IOPerspectiveItem> getPerspectiveItemByAlias(String alias) {
		return Optional.ofNullable(DAO.create(IOPerspectiveItem.class).lookupByAlias(alias));
    }

	public Optional<ODocument> getPerspectiveItemByAliasAsDocument(ODatabaseDocument db, String alias) {
		return getPerspectiveItemByAlias(alias).map(IOPerspectiveItem::getDocument);
    }
	
	public ODocument getDefaultPerspectiveSafe(OSecurityUser user) {
		try {
			return getDefaultPerspective(user);
		} catch (OSecurityAccessException exc) {
			return null;
		}
	}
	
	public ODocument getDefaultPerspective(OSecurityUser user) {
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
		return getPerspectiveByAliasAsDocument(ALIAS_PERSPECTIVE_DEFAULT)
					// Restore default perspective if it was not found
				.orElseGet(() -> DBClosure.sudo((adminDb)->createDefaultPerspective().getDocument()));
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
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		OSchema schema = db.getMetadata().getSchema();
		if (schema.getClass(IOPerspective.CLASS_NAME) == null || schema.getClass(IOPerspectiveItem.CLASS_NAME) == null) {
			//Repair
			onInstall(app, db);
		}
	}
	
	/**
	 * DAO for OPerspective
	 */
	@ProvidedBy(ODocumentWrapperProvider.class)
	@DAOOClass(value = IOPerspective.CLASS_NAME, nameProperty = "name",
			displayable = {"name", "icon", "homeUrl"})
	public static interface IOPerspective extends IODocumentWrapper {
		public static final String CLASS_NAME = "OPerspective";
		
		@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
		public Map<String, String> getName();
		public IOPerspective setName(Map<String, String> value);
		
		@DAOField(notNull = true)
		@DAOFieldIndex(type = OClass.INDEX_TYPE.UNIQUE)
		public String getAlias();
		public IOPerspective setAlias(String value);
		
		public String getIcon();
		public IOPerspective setIcon(String value);
		
		public String getHomeUrl();
		public IOPerspective setHomeUrl(String value);
		
		@DAOField(value = "menu", inverse = "perspective", visualization = UIVisualizersRegistry.VISUALIZER_TABLE)
		public List<IOPerspectiveItem> getMenu();
		public IOPerspective setMenu(List<IOPerspectiveItem> value);
		
		@DAOField(value = "menu")
		public List<ODocument> getMenuAsDocuments();
		@DAOField(value = "menu")
		public IOPerspective setMenuAsDocuments(List<ODocument> value);
		
		@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_TEXTAREA)
		public String getFooter();
		public void setFooter(String value);
		
		@DAOField(type=OType.EMBEDDEDSET)
		public Collection<String> getFeatures();
		public IOPerspective setFeatures(Collection<String> features);
		
		public default boolean providesFeature(String feature) {
			Collection<String> features = getFeatures();
			return features!=null?features.contains(feature):false;
		}
		
		@Lookup("select from "+CLASS_NAME+" where alias = :alias")
		public IOPerspective lookupByAlias(String alias);
		
		public default IOPerspectiveItem getOrCreatePerspectiveItem(String alias, String nameKey, String icon, String url) {
			IOPerspectiveItem item = DAO.create(IOPerspectiveItem.class);
			
			if(item.lookupByAlias(alias)==null) {
				item.setName(CommonUtils.getLocalizedStrings(nameKey))
					.setAlias(alias)
					.setIcon(icon)
					.setUrl(url)
					.setPerspective(this)
					.save();
			}
			return item;
		}
		
		public static IOPerspective getOrCreateByAlias(String alias, String nameKey, String icon, String url) {
			IOPerspective perspective = DAO.create(IOPerspective.class);
			if(perspective.lookupByAlias(alias)==null) {
				perspective.setAlias(alias)
							.setName(CommonUtils.getLocalizedStrings(nameKey))
							.setIcon(icon)
							.setHomeUrl(url)
						    .save();
			}
			return perspective;
		}
	}
	
	/**
	 * DAO for OPerspective
	 * 		public static final String PROP_NAME             = "name";
		public static final String PROP_ALIAS            = "alias";
		public static final String PROP_ICON             = "icon";
		public static final String PROP_URL              = "url";
		public static final String PROP_PERSPECTIVE      = "perspective";
		public static final String PROP_PERSPECTIVE_ITEM = "perspectiveItem";
		public static final String PROP_SUB_ITEMS        = "subItems";
	 */
	@ProvidedBy(ODocumentWrapperProvider.class)
	@DAOOClass(value = IOPerspectiveItem.CLASS_NAME, nameProperty = "name",
					displayable = {"name", "icon", "url"})
	public static interface IOPerspectiveItem extends IODocumentWrapper {
		public static final String CLASS_NAME = "OPerspectiveItem";
		
		@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_LOCALIZATION)
		public Map<String, String> getName();
		public IOPerspectiveItem setName(Map<String, String> value);
		
		@DAOField(notNull = true)
		public String getAlias();
		public IOPerspectiveItem setAlias(String value);
		
		public String getIcon();
		public IOPerspectiveItem setIcon(String value);
		
		public String getUrl();
		public IOPerspectiveItem setUrl(String value);
		
		@DAOField(inverse = "menu")
		public IOPerspective getPerspective();
		public IOPerspectiveItem setPerspective(IOPerspective value);
		
		@DAOField(value = "perspective")
		public ODocument getPerspectiveAsDocument();
		@DAOField(value = "perspective")
		public IOPerspectiveItem setPerspectiveAsDocument(ODocument value);
		
		
		@DAOField(inverse = "subItems")
		public IOPerspectiveItem getPerspectiveItem();
		public IOPerspectiveItem setPerspectiveItem(IOPerspectiveItem value);
		
		@DAOField(inverse = "perspectiveItem", visualization = UIVisualizersRegistry.VISUALIZER_TABLE)
		public List<IOPerspectiveItem> getSubItems();
		public IOPerspectiveItem setSubItems(List<IOPerspectiveItem> value);
		
		@DAOField(value = "subItems")
		public List<ODocument> getSubItemsAsDocuments();
		@DAOField(value = "subItems")
		public IOPerspectiveItem setSubItemsAsDocuments(List<ODocument> value);
		
		@Lookup("select from "+CLASS_NAME+" where alias = :alias")
		public IOPerspectiveItem lookupByAlias(String alias);
	}
	
}
