package ru.ydn.orienteer.components.properties;

import java.lang.reflect.Constructor;
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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import ru.ydn.orienteer.components.properties.visualizers.IVisualizer;
import ru.ydn.orienteer.components.properties.visualizers.ListboxVisualizer;
import ru.ydn.orienteer.components.properties.visualizers.SimpleVisualizer;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class UIVisualizersRegistry
{
	private Table<OType, String, IVisualizer> registryTable = HashBasedTable.create();
	
	public UIVisualizersRegistry()
	{
		registerUIComponentFactory(new SimpleVisualizer("textarea", MultiLineLabel.class, TextArea.class, OType.STRING));
		registerUIComponentFactory(new SimpleVisualizer("table", true, LinksPropertyDataTablePanel.class, LinksPropertyDataTablePanel.class, OType.LINKLIST, OType.LINKSET, OType.LINKBAG));
		registerUIComponentFactory(new ListboxVisualizer());
	}
	
	public Table<OType, String, IVisualizer> getRegistryTable()
	{
		return registryTable;
	}
	
	public void registerUIComponentFactory(IVisualizer visualizer)
	{
		for(OType oType : visualizer.getSupportedTypes())
		{
			registryTable.put(oType, visualizer.getName(), visualizer);
		}
	}
	
	public IVisualizer getComponentFactory(OType oType, String componentName)
	{
		Args.notNull(oType, "oType");
		Args.notNull(componentName, "componentName");
		return registryTable.get(oType, componentName);
	}
	
	public List<String> getComponentsOptions(OType oType)
	{
		List<String> ret = new ArrayList<String>();
		if(oType!=null)
		{
			ret.addAll(registryTable.row(oType).keySet());
		}
		else
		{
			ret.addAll(registryTable.columnKeySet());
		}
		Collections.sort(ret);
		return ret;
	}
	
}
