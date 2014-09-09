package ru.ydn.orienteer.components.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class UIComponentsRegistry
{
	public static interface IUIComponentFactory
	{
		public String getName();
		public boolean isExtended();
		public <T> Component createComponent(String id, IModel<T> model);
	}
	
	public static class DefaultIOComponentFactory implements IUIComponentFactory
	{
		private final String name;
		private final boolean extended;
		private final Class<? extends Component> componentClass;
		
		public DefaultIOComponentFactory(String name, boolean extended, Class<? extends Component> componentClass)
		{
			Args.notNull(name, "name");
			Args.notNull(componentClass, "componentClass");
			this.name = name;
			this.extended = extended;
			this.componentClass = componentClass;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public boolean isExtended() {
			return extended;
		}

		@Override
		public <T> Component createComponent(String id, IModel<T> model) {
			try
			{
				return componentClass.getConstructor(String.class, IModel.class).newInstance(id, model);
			} catch (Exception e)
			{
				throw new WicketRuntimeException("Can't create component", e);
			} 
		}
		
	}
	
	private Table<DisplayMode, OType, Map<String, IUIComponentFactory>> registryTable = HashBasedTable.create();
	
	public UIComponentsRegistry()
	{
		registerUIComponentFactory(new DefaultIOComponentFactory("textarea", false, TextArea.class), DisplayMode.EDIT, OType.STRING);
	}
	
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
	
	public IUIComponentFactory getComponentFactory(DisplayMode mode, OType oType, String name)
	{
		Map<String, IUIComponentFactory> map = registryTable.get(mode, oType);
		return map!=null?map.get(name):null;
	}
	
	public List<String> getComponentsOptions(DisplayMode mode, OType oType)
	{
		SortedSet<String> ret = new TreeSet<String>();
		if(mode!=null || oType!=null)
		{
			if(mode==null)
			{
				for(Map<String, IUIComponentFactory> map : registryTable.column(oType).values())
				{
					ret.addAll(map.keySet());
				}
			}
			else if(oType==null)
			{
				for(Map<String, IUIComponentFactory> map : registryTable.row(mode).values())
				{
					ret.addAll(map.keySet());
				}
			}
			else
			{
				Map<String, IUIComponentFactory> map = registryTable.get(mode, oType);
				if(map!=null) ret.addAll(map.keySet());
			}
		}
		return new ArrayList<String>(ret);
	}
	
}
