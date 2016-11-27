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
	/**
	 * {@link ORecordHook} to catch modules configuration changes
	 *
	 */
	public static class OModulesHook extends ODocumentHookAbstract{

		public OModulesHook(ODatabaseDocument database) {
			super(database);
			setIncludeClasses(IOrienteerModule.OMODULE_CLASS);
		}
		
		@Override
		public void onRecordAfterUpdate(ODocument iDocument) {
			OrienteerWebApplication app = OrienteerWebApplication.lookupApplication();
			if(app!=null) {
				String moduleName = iDocument.field(IOrienteerModule.OMODULE_NAME);
				IOrienteerModule module = app.getModuleByName(moduleName);
				if(module!=null) {
					ODatabaseDocument db = iDocument.getDatabase();
					Object previousActivate = iDocument.getOriginalValue(IOrienteerModule.OMODULE_ACTIVATE);
					Object activated = iDocument.field(IOrienteerModule.OMODULE_ACTIVATE);
					boolean active = activated==null || Boolean.TRUE.equals(activated);
					if(previousActivate!=null && !previousActivate.equals(activated)) {
						if(active) module.onInitialize(app, db, iDocument);
						else module.onDestroy(app, db, iDocument);
					}
					if(active) module.onConfigurationChange(app, db, iDocument);
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
		helper.oClass(IOrienteerModule.OMODULE_CLASS)
				.oProperty(IOrienteerModule.OMODULE_NAME, OType.STRING, 0).markDisplayable().markAsDocumentName()
				.oProperty(IOrienteerModule.OMODULE_VERSION, OType.INTEGER, 10).markDisplayable()
				.oProperty(IOrienteerModule.OMODULE_ACTIVATE, OType.BOOLEAN, 20).markDisplayable().defaultValue("true");
		db.command(new OCommandSQL("update "+IOrienteerModule.OMODULE_CLASS+" set "+IOrienteerModule.OMODULE_ACTIVATE+" = true where "+
										IOrienteerModule.OMODULE_ACTIVATE +" is null")).execute();
	}
	
	private Map<String, ODocument> getInstalledModules(ODatabaseDocument db) {
		Map<String, ODocument> installedModules = new HashMap<String, ODocument>();
		for(ODocument doc : db.browseClass(IOrienteerModule.OMODULE_CLASS))
		{
			installedModules.put((String)doc.field(IOrienteerModule.OMODULE_NAME), doc);
		}
		return installedModules;
	}
	
	protected void loadOrienteerModules(OrienteerWebApplication app, ODatabaseDocument db) {
		Map<String, ODocument> installedModules = getInstalledModules(db);
		
		for(IOrienteerModule module: app.getRegisteredModules())
		{
			String name = module.getName();
			int version = module.getVersion();
			ODocument moduleDoc = installedModules.get(name);
			Integer oldVersion = moduleDoc!=null?(Integer)moduleDoc.field(IOrienteerModule.OMODULE_VERSION, Integer.class):null;
			if(moduleDoc==null || oldVersion==null)
			{
				moduleDoc = module.onInstall(app, db);
				if(moduleDoc==null) moduleDoc = new ODocument(IOrienteerModule.OMODULE_CLASS);
				moduleDoc.field(IOrienteerModule.OMODULE_NAME, module.getName());
				moduleDoc.field(IOrienteerModule.OMODULE_VERSION, module.getVersion());
				moduleDoc.save();
			}
			else if(oldVersion<version)
			{
				ODocument temp = module.onUpdate(app, db, moduleDoc, oldVersion, version);
				if(temp!=null) moduleDoc = temp;
				moduleDoc.field(IOrienteerModule.OMODULE_VERSION, version);
				moduleDoc.save();
			}
			Boolean activate = moduleDoc.field(IOrienteerModule.OMODULE_ACTIVATE);
			if(activate==null || activate) module.onInitialize(app, db, moduleDoc);
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
			for(IOrienteerModule module: app.getRegisteredModules())
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
