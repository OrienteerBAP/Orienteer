package ru.ydn.orienteer.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.UIComponentsRegistry;
import ru.ydn.orienteer.components.table.CheckBoxColumn;
import ru.ydn.orienteer.components.table.OEntityColumn;
import ru.ydn.orienteer.components.table.OPropertyValueColumn;
import ru.ydn.orienteer.services.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.utils.ODocumentORIDConverter;

public class OClassIntrospector implements IOClassIntrospector
{
	public static class PropertyDisplayablePredicate implements Predicate<OProperty>
	{
		public static final PropertyDisplayablePredicate INSTANCE = new PropertyDisplayablePredicate();
		@Override
		public boolean apply(OProperty input) {
			Boolean value = CustomAttributes.DISPLAYABLE.getValue(input);
			return value!=null?value:false;
		}
	}
	
	public static class GetOrderOfPropertyFunction implements Function<OProperty, Integer>
	{
		public static final GetOrderOfPropertyFunction INSTANCE = new GetOrderOfPropertyFunction();
		@Override
		public Integer apply(OProperty input) {
			String order = input.getCustom(CustomAttributes.ORDER.getName());
			try
			{
				return order!=null?Integer.parseInt(order):null;
			} catch (NumberFormatException e)
			{
				return null;
			}
		}
		
	}
	
	public static final Ordering<OProperty> ORDER_PROPERTIES_BY_ORDER = Ordering.<Integer>natural().nullsLast().onResultOf(GetOrderOfPropertyFunction.INSTANCE);

	
	@Override
	public List<OProperty> getDisplayableProperties(OClass oClass) {
		Collection<OProperty> properties =  oClass.properties();
		Collection<OProperty> filteredProperties = Collections2.filter(properties, PropertyDisplayablePredicate.INSTANCE);
		if(filteredProperties==null || filteredProperties.isEmpty()) filteredProperties = properties;
		return ORDER_PROPERTIES_BY_ORDER.sortedCopy(filteredProperties);
	}

	@Override
	public List<IColumn<ODocument, String>> getColumnsFor(OClass oClass) {
		List<OProperty> properties = getDisplayableProperties(oClass);
		List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>(properties.size()+2);
		columns.add(new CheckBoxColumn<ODocument, ORID, String>(null, ODocumentORIDConverter.INSTANCE));
		OEntityColumn entityColumn = new OEntityColumn(oClass);
		String nameProperty = entityColumn.getNameProperty();
		columns.add(entityColumn);
		for (OProperty oProperty : properties)
		{
			if(nameProperty==null || !nameProperty.equals(oProperty.getName()))columns.add(new OPropertyValueColumn(oProperty));
		}
		return columns;
	}

	@Override
	public List<ODocument> getNavigationPath(ODocument doc, boolean fromUpToDown) {
		List<ODocument> path = new ArrayList<ODocument>();
		ODocument current = doc;
		while(current!=null)
		{
			path.add(current);
			current = getParent(current);
		}
		return fromUpToDown?Lists.reverse(path):path;
	}

	@Override
	public ODocument getParent(ODocument doc) {
		if(doc==null || doc.getSchemaClass()==null) return null;
		OClass oClass = doc.getSchemaClass();
		OProperty parent = CustomAttributes.PROP_PARENT.getValue(oClass);
		if(parent!=null) return doc.field(parent.getName());
		else return null;
	}

	@Override
	public List<String> listTabs(OClass oClass) {
		Set<String> tabs = new HashSet<String>();
		for(OProperty property: oClass.properties())
		{
			String tab = CustomAttributes.TAB.getValue(property);
			if(tab==null) tab = DEFAULT_TAB;
			tabs.add(tab);
		}
		return new ArrayList<String>(tabs);
	}

	@Override
	public List<OProperty> listProperties(OClass oClass, String tab, final Boolean extended) {
		Collection<OProperty> properties =  oClass.properties();
		final String safeTab = tab!=null?tab:DEFAULT_TAB;
		final UIComponentsRegistry registry = OrienteerWebApplication.get().getUIComponentsRegistry();
		Collection<OProperty> filteredProperties = Collections2.filter(properties, new Predicate<OProperty>() {

			@Override
			public boolean apply(OProperty input) {
				String propertyTab = CustomAttributes.TAB.getValue(input);
				boolean ret = safeTab.equals(propertyTab!=null?propertyTab:DEFAULT_TAB);
				ret = ret && !CustomAttributes.HIDDEN.getValue(input, false);
				if(!ret || extended==null) return ret;
				else {
					String component = CustomAttributes.VISUALIZATION_TYPE.getValue(input);
					if(component==null) return !extended;
					return registry.getComponentFactory(input.getType(), component).isExtended() == extended;
				}
			}
		});
		//if(filteredProperties==null || filteredProperties.isEmpty()) filteredProperties = properties;
		return ORDER_PROPERTIES_BY_ORDER.sortedCopy(filteredProperties);
	}
	
	


}
