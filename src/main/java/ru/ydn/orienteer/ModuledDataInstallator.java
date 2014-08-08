package ru.ydn.orienteer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.AbstractDataInstallator;

public class ModuledDataInstallator extends AbstractDataInstallator
{
	private static final String OMODULE_CLASS = "OModule";
	private static final String OMODULE_NAME = "name";
	private static final String OMODULE_VERSION = "version";
	
	private Map<String, IOrienteerModule> registeredModules = new LinkedHashMap<String, IOrienteerModule>();
	
	public void registerModule(IOrienteerModule module)
	{
		registeredModules.put(module.getName(), module);
	}
	
	@Override
	protected void installData(ODatabaseRecord database) {
		ODatabaseDocument db = (ODatabaseDocument)database;
		OSchema schema = db.getMetadata().getSchema();
		OClass oModuleClass = schema.getClass(OMODULE_CLASS);
		if(oModuleClass==null)
		{
			oModuleClass = schema.createClass(OMODULE_CLASS);
		}
		if(!oModuleClass.existsProperty(OMODULE_NAME))
		{
			oModuleClass.createProperty(OMODULE_NAME, OType.STRING);
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
		
		for(Map.Entry<String, IOrienteerModule> entry: registeredModules.entrySet())
		{
			String name = entry.getKey();
			IOrienteerModule module = entry.getValue();
			int version = module.getVersion();
			Integer oldVersion = installedModules.get(name);
			if(oldVersion==null)
			{
				module.onInstall(db);
			}
			else if(!oldVersion.equals(version))
			{
				module.onUpdate(db, oldVersion, version);
			}
		}
	}

}
