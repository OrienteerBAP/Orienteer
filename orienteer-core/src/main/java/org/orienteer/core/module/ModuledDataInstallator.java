package org.orienteer.core.module;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.util.lang.Objects;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;

import ru.ydn.wicket.wicketorientdb.AbstractDataInstallator;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

/**
 * Data installator of modules specific classes
 */
public class ModuledDataInstallator extends AbstractDataInstallator
{
	private static final Logger LOG = LoggerFactory.getLogger(ModuledDataInstallator.class);
	public static final String OMODULE_CLASS = "OModule";
	public static final String OMODULE_NAME = "name";
	public static final String OMODULE_VERSION = "version";
	public static final String OMODULE_ACTIVATED = "activated";
	
	/**
	 * {@link ORecordHook} to catch modules configuration changes
	 *
	 */
	public static class OModulesHook extends ODocumentHookAbstract{

		public OModulesHook(ODatabaseDocument database) {
			super(database);
			setIncludeClasses(OMODULE_CLASS);
		}
		
		@Override
		public void onRecordAfterUpdate(ODocument iDocument) {
			OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
			if(app!=null) {
				String moduleName = iDocument.field(OMODULE_NAME);
				IOrienteerModule module = app.getModuleByName(moduleName);
				if(module!=null) {
					ODatabaseDocument db = iDocument.getDatabase();
					if(!Objects.isEqual(iDocument.getOriginalValue(OMODULE_ACTIVATED), iDocument.field(OMODULE_ACTIVATED))) {
						Object activated = iDocument.field(OMODULE_ACTIVATED);
						if(activated==null || Boolean.TRUE.equals(activated)) module.onInitialize(app, db, iDocument);
						else module.onDestroy(app, db, iDocument);
					}
					module.onConfigurationChange(app, db, iDocument);
				}
			}
		}

		@Override
		public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
			return DISTRIBUTED_EXECUTION_MODE.BOTH;
		}
		
	}
	
	@Override
	protected void installData(OrientDbWebApplication application, ODatabaseDocument database) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		ODatabaseDocument db = (ODatabaseDocument)database;
		updateOModuleSchema(db);
		loadOrienteerModules(app, db);
		app.getOrientDbSettings().getORecordHooks().add(OModulesHook.class);
	}
	
	protected void updateOModuleSchema(ODatabaseDocument db) {
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oClass(OMODULE_CLASS)
				.oProperty(OMODULE_NAME, OType.STRING, 0).markDisplayable().markAsDocumentName()
				.oProperty(OMODULE_VERSION, OType.INTEGER, 10).markDisplayable()
				.oProperty(OMODULE_ACTIVATED, OType.BOOLEAN, 20).markDisplayable().defaultValue("true");
		db.command(new OCommandSQL("update "+OMODULE_CLASS+" set "+OMODULE_ACTIVATED+" = true where "+OMODULE_ACTIVATED +" is null")).execute();
	}
	
	private Map<String, ODocument> getInstalledModules(ODatabaseDocument db) {
		Map<String, ODocument> installedModules = new HashMap<String, ODocument>();
		for(ODocument doc : db.browseClass(OMODULE_CLASS))
		{
			installedModules.put((String)doc.field(OMODULE_NAME), doc);
		}
		return installedModules;
	}
	
	protected void loadOrienteerModules(OrienteerWebApplication app, ODatabaseDocument db) {
		Map<String, ODocument> installedModules = getInstalledModules(db);
		
		for(Map.Entry<String, IOrienteerModule> entry: app.getRegisteredModules().entrySet())
		{
			String name = entry.getKey();
			IOrienteerModule module = entry.getValue();
			int version = module.getVersion();
			ODocument moduleDoc = installedModules.get(name);
			Integer oldVersion = moduleDoc!=null?(Integer)moduleDoc.field(OMODULE_VERSION, Integer.class):null;
			if(moduleDoc==null || oldVersion==null)
			{
				moduleDoc = module.onInstall(app, db);
				if(moduleDoc==null) moduleDoc = new ODocument(OMODULE_CLASS);
				moduleDoc.field(OMODULE_NAME, module.getName());
				moduleDoc.field(OMODULE_VERSION, module.getVersion());
				moduleDoc.save();
			}
			else if(oldVersion<version)
			{
				ODocument temp = module.onUpdate(app, db, moduleDoc, oldVersion, version);
				if(temp!=null) moduleDoc = temp;
				moduleDoc.field(OMODULE_VERSION, version);
				moduleDoc.save();
			}
			Boolean activated = moduleDoc.field(OMODULE_ACTIVATED);
			if(activated==null || activated) module.onInitialize(app, db, moduleDoc);
		}
	}

	@Override
	public void onBeforeDestroyed(Application application) {
		super.onBeforeDestroyed(application);
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		ODatabaseDocument db = (ODatabaseDocument)getDatabase(app);
		try
		{
			Map<String, ODocument> installedModules = getInstalledModules(db);
			for(IOrienteerModule module: app.getRegisteredModules().values())
			{
				try
				{
					db.begin();
					module.onDestroy(app, db, installedModules.get(module.getName()));
					db.commit();
				} catch (Exception e)
				{
					LOG.error("Exception during destroying module '"+module.getName()+"'", e);
					db.rollback();
				}
			}
		} 
		finally
		{
			db.close();
		}
	}
	
	

}
