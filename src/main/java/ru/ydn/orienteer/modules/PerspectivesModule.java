package ru.ydn.orienteer.modules;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class PerspectivesModule extends AbstractOrienteerModule
{
	public static final String OCLASS_PERSPECTIVE="OPerspective";
	public static final String OCLASS_ITEM = "OPerspectiveItem";

	public PerspectivesModule()
	{
		super("prespectives", 1);
	}

	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		OSchema schema = db.getMetadata().getSchema();
		OClass perspectiveClass = mergeOClass(schema, OCLASS_PERSPECTIVE);
		OClass itemClass = mergeOClass(schema, OCLASS_ITEM);
		mergeOProperty(perspectiveClass, "name", OType.STRING);
		OProperty menu = mergeOProperty(perspectiveClass, "menu", OType.LINKLIST, "table").setLinkedClass(itemClass);
		mergeOProperty(perspectiveClass, "footer", OType.STRING, "textarea");
		assignNameAndParent(perspectiveClass, "name", null);
		switchDisplayable(perspectiveClass, true, "name");
		orderProperties(perspectiveClass, "name", "footer", "menu");
		
		mergeOProperty(itemClass, "name", OType.STRING);
		mergeOProperty(itemClass, "icon", OType.STRING);
		mergeOProperty(itemClass, "url", OType.STRING);
		OProperty perspective = mergeOProperty(itemClass, "perspective", OType.LINK).setLinkedClass(perspectiveClass);
		assignNameAndParent(itemClass, "name", "perspective");
		switchDisplayable(itemClass, true, "name", "icon", "url");
		orderProperties(itemClass, "name", "perspective", "icon", "url");
		
		CustomAttributes.PROP_INVERSE.setValue(menu, perspective);
		CustomAttributes.PROP_INVERSE.setValue(perspective, menu);
	}

}
