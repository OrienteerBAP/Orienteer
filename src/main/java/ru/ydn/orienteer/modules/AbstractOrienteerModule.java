package ru.ydn.orienteer.modules;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class AbstractOrienteerModule implements IOrienteerModule
{
	private final String name;
	private final int version;
	
	protected AbstractOrienteerModule(String name, int version)
	{
		this.name = name;
		this.version = version;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public void onInstall(OrienteerWebApplication app, ODatabaseDocument db) {

	}

	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db,
			int oldVersion, int newVersion) {

	}

	@Override
	public void onUninstall(OrienteerWebApplication app, ODatabaseDocument db) {

	}

	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {

	}

	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {

	}
	
	protected OClass mergeOClass(OSchema schema, String className)
	{
		OClass ret = schema.getClass(className);
		if(ret==null)
		{
			ret = schema.createClass(className);
		}
		return ret;
	}
	
	protected OProperty mergeOProperty(OClass oClass, String propertyName, OType type)
	{
		OProperty ret = oClass.getProperty(propertyName);
		if(ret==null)
		{
			ret = oClass.createProperty(propertyName, type);
		}
		else
		{
			if(!type.equals(ret.getType()))
			{
				ret.setType(type);
			}
		}
		return ret;
	}
	
	protected OIndex<?> mergeOIndex(OClass oClass, String name, INDEX_TYPE type, String... fields)
	{
		OIndex<?> ret = oClass.getClassIndex(name);
		if(ret==null)
		{
			ret = oClass.createIndex(name, type, fields);
		}
		else
		{
			//We can't do something to change type and fields if required
		}
		return ret;
	}
	
	protected void orderProperties(OClass oClass, String... fields)
	{
		for(int i=0; i<fields.length; i++)
		{
			String field = fields[i];
			OProperty oProperty = oClass.getProperty(field);
			if(oProperty!=null)
			{
				CustomAttributes.ORDER.setValue(oProperty, i*10);
			}
		}
	}
	
	protected void assignTab(OClass oClass, String tab, String... fields)
	{
		updateCustomAttribute(oClass, CustomAttributes.TAB, tab, fields);
	}
	
	protected void assignVisualization(OClass oClass, String visualization, String... fields)
	{
		updateCustomAttribute(oClass, CustomAttributes.VISUALIZATION_TYPE, visualization, fields);
	}
	
	protected void switchDisplayable(OClass oClass, boolean displayable, String... fields)
	{
		updateCustomAttribute(oClass, CustomAttributes.DISPLAYABLE, displayable, fields);
	}
	
	protected <V> void updateCustomAttribute(OClass oClass, CustomAttributes attr, V value, String... fields)
	{
		for(String field: fields)
		{
			OProperty oProperty = oClass.getProperty(field);
			if(oProperty!=null)
			{
				attr.setValue(oProperty, value);
			}
		}
	}
	
	protected void assignNameAndParent(OClass oClass, String nameField, String parentField)
	{
		OProperty name = nameField!=null?oClass.getProperty(nameField):null;
		OProperty parent = parentField!=null?oClass.getProperty(parentField):null;
		if(name!=null)
		{
			CustomAttributes.PROP_NAME.setValue(oClass, name);
		}
		if(parent!=null)
		{
			CustomAttributes.PROP_PARENT.setValue(oClass, parent);
		}
	}

}
