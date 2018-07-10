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
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.core.util.OSchemaHelper;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.inject.Singleton;
import java.util.List;
import java.util.Set;

/**
 * {@link IOrienteerModule} for "perspectives" feature of Orienteer
 */
@Singleton
public class PerspectivesModule extends AbstractOrienteerModule {
	public static final String NAME = "perspectives";
	public static final String OCLASS_PERSPECTIVE="OPerspective";
	public static final String OCLASS_ITEM = "OPerspectiveItem";

	public static final String DEFAULT_PERSPECTIVE = "Default";

	public static final String PROP_PERSPECTIVE = "perspective";

	public PerspectivesModule()
	{
		super(NAME, 5);
	}

	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper.bind(db)
			.oClass(OCLASS_PERSPECTIVE)
				.oProperty("name", OType.EMBEDDEDMAP).assignVisualization("localization")
					.markAsDocumentName()
					.linkedType(OType.STRING)
					.oIndex(OCLASS_PERSPECTIVE + ".name_by_value", INDEX_TYPE.UNIQUE, "name by value")
				.oProperty("icon", OType.STRING)
				.oProperty("homeUrl", OType.STRING)
				.oProperty("menu", OType.LINKLIST).assignVisualization("table")
				.oProperty("footer", OType.STRING).assignVisualization("textarea")
				.switchDisplayable(true, "name", "homeUrl")
				.orderProperties("name", "icon", "homeUrl", "footer", "menu")
			.oClass(OCLASS_ITEM)
				.oProperty("name", OType.EMBEDDEDMAP).assignVisualization("localization").markAsDocumentName()
				.oProperty("icon", OType.STRING)
				.oProperty("url", OType.STRING)
				.oProperty("perspective", OType.LINK).markAsLinkToParent()
				.oProperty("perspectiveItem", OType.LINK).markAsLinkToParent()
				.oProperty("subItems", OType.LINKLIST).assignVisualization("table")
				.switchDisplayable(true, "name", "icon", "url")
				.orderProperties("name", "perspective", "icon", "url")
				.setupRelationship(OCLASS_PERSPECTIVE, "menu", OCLASS_ITEM, "perspective")
			.oClass(OIdentity.CLASS_NAME)
				.oProperty(PROP_PERSPECTIVE, OType.LINK).linkedClass(OCLASS_PERSPECTIVE)
			.setupRelationship(OCLASS_ITEM, "subItems", OCLASS_ITEM, "perspectiveItem")
				.oProperty("perspectiveItem", OType.LINK).linkedClass(OCLASS_ITEM)
            .oClass(OIdentity.CLASS_NAME);
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {
		int toVersion = oldVersion+1;
		switch (toVersion) {
			case 2:
				convertNameProperty(app, db, OCLASS_PERSPECTIVE);
				convertNameProperty(app, db, OCLASS_ITEM);
				break;
			case 3:
				onInstall(app, db);
				break;
			case 4:
				OIndex<?> index = db.getMetadata().getIndexManager().getIndex(OCLASS_PERSPECTIVE + ".name");
				if(index!=null) index.delete();
				onInstall(app, db);
			case 5:
				OSchemaHelper.bind(db)
					.oClass(OIdentity.CLASS_NAME)
					.oProperty("perspective", OType.LINK).linkedClass(OCLASS_PERSPECTIVE);
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
	
	private ODocument runtimeRepairDefaultPerspective()
	{
		return new DBClosure<ODocument>() {

			@Override
			protected ODocument execute(ODatabaseDocument db) {
				ODocument perspective = new ODocument(OCLASS_PERSPECTIVE);
				perspective.field("name", CommonUtils.toMap("en", DEFAULT_PERSPECTIVE));
				perspective.field("homeUrl", "/classes");
				perspective.save();
				
				ODocument item = new ODocument(OCLASS_ITEM);
				item.field("name", CommonUtils.toMap("en", "Users"));
				item.field("icon", "users");
				item.field("url", "/browse/OUser");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", CommonUtils.toMap("en", "Roles"));
				item.field("icon", "users");
				item.field("url", "/browse/ORole");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", CommonUtils.toMap("en", "Schema"));
				item.field("icon", "cubes");
				item.field("url", "/schema");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", CommonUtils.toMap("en", "Localization"));
				item.field("icon", "language");
				item.field("url", "/browse/OLocalization");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", CommonUtils.toMap("en", "Perspectives"));
				item.field("icon", "desktop");
				item.field("url", "/browse/OPerspective");
				item.field("perspective", perspective);
				item.save();

				return perspective;
			}
		}.execute();
	}
	
	public ODocument getPerspectiveByName(ODatabaseDocument db, String name)
	{
		List<ODocument> perspectives = db.query(new OSQLSynchQuery<ODocument>("select from "+OCLASS_PERSPECTIVE+" where name CONTAINSVALUE ?"), name);
		if(perspectives!=null && !perspectives.isEmpty())
		{
			return perspectives.get(0);
		}
		else
		{
			return null;
		}
	}
	
	public ODocument getDefaultPerspective(ODatabaseDocument db, OSecurityUser user)
	{
		if(user!=null)
		{
			Object perspectiveObj = user.getDocument().field("perspective");
			if(perspectiveObj!=null && perspectiveObj instanceof OIdentifiable) 
				return (ODocument)((OIdentifiable)perspectiveObj).getRecord();
			Set<? extends OSecurityRole> roles = user.getRoles();
			ODocument perspective = null;
			for (OSecurityRole oRole : roles)
			{
				perspective = getPerspectiveForORole(oRole);
				if(perspective!=null) return perspective;
			}
		}
		ODocument perspective = getPerspectiveByName(db, DEFAULT_PERSPECTIVE);
		if(perspective==null)
		{
			perspective = runtimeRepairDefaultPerspective();
		}
		return perspective;
	}
	
	private ODocument getPerspectiveForORole(OSecurityRole role)
	{
		if(role==null) return null;
		Object perspectiveObj = role.getDocument().field("perspective");
		if(perspectiveObj!=null && perspectiveObj instanceof OIdentifiable) 
			return (ODocument)((OIdentifiable)perspectiveObj).getRecord();
		else
		{
			OSecurityRole parentRole = role.getParentRole();
			if(parentRole!=null && !parentRole.equals(role))
			{
				return getPerspectiveForORole(parentRole);
			}
			else
			{
				return null;
			}
		}
	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema  = db.getMetadata().getSchema();
		if(schema.getClass(OCLASS_PERSPECTIVE)==null || schema.getClass(OCLASS_ITEM)==null)
		{
			//Repair
			onInstall(app, db);
		}
	}

}
