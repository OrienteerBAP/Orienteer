package org.orienteer.modules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.Application;
import org.orienteer.CustomAttributes;
import org.orienteer.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.AbstractDataInstallator;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

public class ModuledDataInstallator extends AbstractDataInstallator
{
	private static final Logger LOG = LoggerFactory.getLogger(ModuledDataInstallator.class);
	private static final String OMODULE_CLASS = "OModule";
	private static final String OMODULE_NAME = "name";
	private static final String OMODULE_VERSION = "version";
	
	@Override
	protected void installData(OrientDbWebApplication application, ODatabaseDocument database) {
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		ODatabaseDocument db = (ODatabaseDocument)database;
		OSchema schema = db.getMetadata().getSchema();
		OClass oModuleClass = schema.getClass(OMODULE_CLASS);
		if(oModuleClass==null)
		{
			oModuleClass = schema.createClass(OMODULE_CLASS);
		}
		if(!oModuleClass.existsProperty(OMODULE_NAME))
		{
			OProperty nameProperty = oModuleClass.createProperty(OMODULE_NAME, OType.STRING);
			CustomAttributes.PROP_NAME.setValue(oModuleClass, nameProperty);
		}
		if(!oModuleClass.existsProperty(OMODULE_VERSION))
		{
			oModuleClass.createProperty(OMODULE_VERSION, OType.INTEGER);
		}
		Map<String, Integer> installedModules = new HashMap<String, Integer>();
		for(ODocument doc : db.browseClass(OMODULE_CLASS))
		{
			installedModules.put((String)doc.field(OMODULE_NAME), (Integer)doc.field(OMODULE_VERSION, Integer.class));
		}
		
		for(Map.Entry<String, IOrienteerModule> entry: app.getRegisteredModules().entrySet())
		{
			String name = entry.getKey();
			IOrienteerModule module = entry.getValue();
			int version = module.getVersion();
			Integer oldVersion = installedModules.get(name);
			if(oldVersion==null)
			{
				module.onInstall(app, db);
				ODocument moduleDoc = new ODocument(oModuleClass);
				moduleDoc.field(OMODULE_NAME, module.getName());
				moduleDoc.field(OMODULE_VERSION, module.getVersion());
				moduleDoc.save();
			}
			else if(!oldVersion.equals(version))
			{
				module.onUpdate(app, db, oldVersion, version);
			}
			module.onInitialize(app, db);
		}
	}

	@Override
	public void onBeforeDestroyed(Application application) {
		super.onBeforeDestroyed(application);
		OrienteerWebApplication app = (OrienteerWebApplication)application;
		ODatabaseDocument db = (ODatabaseDocument)getDatabase(app);
		try
		{
			for(IOrienteerModule module: app.getRegisteredModules().values())
			{
				try
				{
					db.begin();
					module.onDestroy(app, db);
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
