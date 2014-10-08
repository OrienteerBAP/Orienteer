package ru.ydn.orienteer.modules;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class UpdateDefaultSchemaModule extends AbstractOrienteerModule
{
	public UpdateDefaultSchemaModule()
	{
		super("update-default-schema", 1);
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
		if(oldVersion+1<newVersion) onUpdate(app, db, oldVersion+1, newVersion);
	}
	
	public void onUpdateToFirstVesion(OrienteerWebApplication app, ODatabaseDocument db)
	{
		OSchema schema = db.getMetadata().getSchema();
		OClass oFunction = schema.getClass("OFunction");
		if(oFunction!=null)
		{
			assignVisualization(oFunction, "textarea", "code");
			orderProperties(oFunction, "name", "language", "idempotent", "parameters", "code");
			switchDisplayable(oFunction, true, "name", "language", "parameters");
			assignNameAndParent(oFunction, "name", null);
		}
		OClass oRestricted = schema.getClass("ORestricted");
		if(oRestricted!=null)
		{
			String[] fields = {"_allow", "_allowRead", "_allowUpdate", "_allowDelete"};
			assignTab(oRestricted, "security", fields);
			assignVisualization(oRestricted, "table", fields);
			orderProperties(oRestricted, fields); 
		}
		OClass oRole = schema.getClass("ORole");
		if(oRole!=null)
		{
			orderProperties(oRole, "name", "mode", "inheritedRole", "rules");
			assignNameAndParent(oRole, "name", "inheritedRole");
			switchDisplayable(oRole, true, "name", "model", "inheritedRole");
		}
		OClass oUser = schema.getClass("OUser");
		if(oUser!=null)
		{
			orderProperties(oUser, "name", "status", "password", "roles");
			assignVisualization(oUser, "table", "roles");
			assignNameAndParent(oUser, "name", null);
			switchDisplayable(oUser, true, "name", "status");
		}
		OClass oSchedule = schema.getClass("OSchedule");
		if(oSchedule!=null)
		{
			orderProperties(oSchedule, "name", "rule", "status", "start", "starttime", "arguments", "function");
			assignNameAndParent(oSchedule, "name", null);
			switchDisplayable(oSchedule, true, "name", "status", "rule");
		}
	}
	
}
