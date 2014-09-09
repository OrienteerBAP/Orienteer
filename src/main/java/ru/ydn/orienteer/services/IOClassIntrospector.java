package ru.ydn.orienteer.services;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;

import ru.ydn.orienteer.components.properties.DisplayMode;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public interface IOClassIntrospector
{
	public static String DEFAULT_TAB = "parameters";
	
	public List<OProperty> getDisplayableProperties(OClass oClass);
	public List<IColumn<ODocument, String>>  getColumnsFor(OClass oClass);
	public List<ODocument> getNavigationPath(ODocument doc, boolean fromUpToDown);
	public ODocument getParent(ODocument doc);
	public List<String> listTabs(OClass oClass);
	public List<OProperty> listProperties(OClass oClass, String tab, DisplayMode mode, Boolean extended);
}
