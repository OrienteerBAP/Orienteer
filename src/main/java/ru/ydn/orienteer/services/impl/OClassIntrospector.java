package ru.ydn.orienteer.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.orienteer.CustomAttributes;
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

}
