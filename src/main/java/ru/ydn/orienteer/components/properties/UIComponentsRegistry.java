package ru.ydn.orienteer.components.properties;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class UIComponentsRegistry
{
	public static interface IUIComponentFactory
	{
		public String getName();
		public <T> Component createComponent(String id, IModel<T> model);
	}
	
	private Table<DisplayMode, OType, Map<String, IUIComponentFactory>> registryTable = HashBasedTable.create();
	
	public Table<DisplayMode, OType, Map<String, IUIComponentFactory>> getRegistryTable()
	{
		return registryTable;
	}
	
	public void registerUIComponentFactory(IUIComponentFactory factory, DisplayMode mode, OType... types)
	{
		for(OType oType : types)
		{
			registerUIComponentFactory(factory, mode, oType);
		}
	}
	
	public void registerUIComponentFactory(IUIComponentFactory factory, DisplayMode mode, OType oType)
	{
		Map<String, IUIComponentFactory> factoriesMap = registryTable.get(mode, oType);
		if(factoriesMap==null)
		{
			factoriesMap = new HashMap<String, UIComponentsRegistry.IUIComponentFactory>();
			registryTable.put(mode, oType, factoriesMap);
		}
		factoriesMap.put(factory.getName(), factory);
	}
}
