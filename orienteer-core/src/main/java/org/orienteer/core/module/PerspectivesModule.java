package org.orienteer.core.module;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.orienteer.core.CustomAttributes;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.util.OSchemaHelper;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@Singleton
public class PerspectivesModule extends AbstractOrienteerModule
{
	public static final String OCLASS_PERSPECTIVE="OPerspective";
	public static final String OCLASS_ITEM = "OPerspectiveItem";
	
	public static final String DEFAULT_PERSPECTIVE = "Default";
	

	public PerspectivesModule()
	{
		super("perspectives", 1);
	}

	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchemaHelper.bind(db)
			.oClass(OCLASS_PERSPECTIVE)
				.oProperty("name", OType.STRING)
					.markAsDocumentName()
					.oIndex(OCLASS_PERSPECTIVE+".name", INDEX_TYPE.UNIQUE)
				.oProperty("icon", OType.STRING)
				.oProperty("homeUrl", OType.STRING)
				.oProperty("menu", OType.LINKLIST).assignVisualization("table")
				.oProperty("footer", OType.STRING).assignVisualization("textarea")
				.switchDisplayable(true, "name", "homeUrl")
				.orderProperties("name", "icon", "homeUrl", "footer", "menu")
			.oClass(OCLASS_ITEM)
				.oProperty("name", OType.STRING).markAsDocumentName()
				.oProperty("icon", OType.STRING)
				.oProperty("url", OType.STRING)
				.oProperty("perspective", OType.LINK).markAsLinkToParent()
				.switchDisplayable(true, "name", "icon", "url")
				.orderProperties("name", "perspective", "icon", "url")
			.setupRelationship(OCLASS_PERSPECTIVE, "menu", OCLASS_ITEM, "perspective")
			.oClass(OSecurityShared.IDENTITY_CLASSNAME)
				.oProperty("perspective", OType.LINK).linkedClass(OCLASS_PERSPECTIVE);
	}
	
	private ODocument runtimeRepairDefaultPerspective()
	{
		return new DBClosure<ODocument>() {

			@Override
			protected ODocument execute(ODatabaseDocument db) {
				ODocument perspective = new ODocument(OCLASS_PERSPECTIVE);
				perspective.field("name", DEFAULT_PERSPECTIVE);
				perspective.field("homeUrl", "/classes");
				perspective.save();
				
				ODocument item = new ODocument(OCLASS_ITEM);
				item.field("name", "Users");
				item.field("icon", "users");
				item.field("url", "/browse/OUser");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", "Roles");
				item.field("icon", "users");
				item.field("url", "/browse/ORole");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", "Schema");
				item.field("icon", "cubes");
				item.field("url", "/classes");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", "Localization");
				item.field("icon", "language");
				item.field("url", "/browse/OLocalization");
				item.field("perspective", perspective);
				item.save();
				
				item = new ODocument(OCLASS_ITEM);
				item.field("name", "Perspectives");
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
		List<ODocument> perspectives = db.query(new OSQLSynchQuery<ODocument>("select from "+OCLASS_PERSPECTIVE+" where name=?"), name);
		if(perspectives!=null && !perspectives.isEmpty())
		{
			return perspectives.get(0);
		}
		else
		{
			return null;
		}
	}
	
	public ODocument getDefaultPerspective(ODatabaseDocument db, OUser user)
	{
		if(user!=null)
		{
			Object perspectiveObj = user.getDocument().field("perspective");
			if(perspectiveObj!=null && perspectiveObj instanceof OIdentifiable) 
				return (ODocument)((OIdentifiable)perspectiveObj).getRecord();
			Set<ORole> roles = user.getRoles();
			ODocument perspective = null;
			for (ORole oRole : roles)
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
	
	private ODocument getPerspectiveForORole(ORole role)
	{
		if(role==null) return null;
		Object perspectiveObj = role.getDocument().field("perspective");
		if(perspectiveObj!=null && perspectiveObj instanceof OIdentifiable) 
			return (ODocument)((OIdentifiable)perspectiveObj).getRecord();
		else
		{
			ORole parentRole = role.getParentRole();
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
