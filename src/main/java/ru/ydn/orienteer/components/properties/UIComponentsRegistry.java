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
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import ru.ydn.orienteer.model.DynamicPropertyValueModel;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class UIComponentsRegistry
{
	public static interface IUIComponentFactory
	{
		public String getName();
		public boolean isExtended();
		public Component createComponent(String id, DisplayMode mode, IModel<ODocument> documentModel, IModel<OProperty> propertyModel);
		public <T> Component createComponent(String id, DisplayMode mode, IModel<T> model);
	}
	
	public static class DefaultIOComponentFactory implements IUIComponentFactory
	{
		private final String name;
		private final boolean extended;
		private final Class<? extends Component> viewComponentClass;
		private final Class<? extends Component> editComponentClass;
		
		public DefaultIOComponentFactory(String name, boolean extended, Class<? extends Component> viewComponentClass, Class<? extends Component> editComponentClass)
		{
			Args.notNull(name, "name");
			Args.notNull(viewComponentClass, "viewComponentClass");
			Args.notNull(editComponentClass, "editComponentClass");
			this.name = name;
			this.extended = extended;
			this.viewComponentClass = viewComponentClass;
			this.editComponentClass = editComponentClass;
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
		public Component createComponent(String id, DisplayMode mode,
				IModel<ODocument> documentModel, IModel<OProperty> propertyModel) {
			Class<? extends Component> componentClass = DisplayMode.EDIT.equals(mode)?editComponentClass:viewComponentClass;
			try
			{
				Constructor<? extends Component> constructor = componentClass.getConstructor(String.class, IModel.class, IModel.class);
				return constructor.newInstance(id, documentModel, propertyModel);
			} catch (NoSuchMethodException e)
			{
				return createComponent(id, mode, new DynamicPropertyValueModel<ODocument>(documentModel, propertyModel));
			} catch (Exception e)
			{
				throw new WicketRuntimeException("Can't create component", e);
			}
		}

		@Override
		public <T> Component createComponent(String id, DisplayMode mode,
				IModel<T> model) {
			Class<? extends Component> componentClass = DisplayMode.EDIT.equals(mode)?editComponentClass:viewComponentClass;
			try
			{
				return componentClass.getConstructor(String.class, IModel.class).newInstance(id, model);
			} catch (Exception e)
			{
				throw new WicketRuntimeException("Can't create component", e);
			} 
		}

	}
	
	private Table<OType, String, IUIComponentFactory> registryTable = HashBasedTable.create();
	
	public UIComponentsRegistry()
	{
		registerUIComponentFactory(new DefaultIOComponentFactory("textarea", false, Label.class, TextArea.class), OType.STRING);
	}
	
	public Table<OType, String, IUIComponentFactory> getRegistryTable()
	{
		return registryTable;
	}
	
	public void registerUIComponentFactory(IUIComponentFactory factory, OType... types)
	{
		for(OType oType : types)
		{
			registerUIComponentFactory(factory, oType);
		}
	}
	
	public void registerUIComponentFactory(IUIComponentFactory factory, OType oType)
	{
		registryTable.put(oType, factory.getName(), factory);
	}
	
	public IUIComponentFactory getComponentFactory(OType oType, String componentName)
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
