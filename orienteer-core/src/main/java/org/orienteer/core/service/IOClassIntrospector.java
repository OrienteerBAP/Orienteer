package org.orienteer.core.service;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.property.DisplayMode;

import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;

import com.google.common.base.Predicate;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Utility service for introspection of {@link OClass}
 */
public interface IOClassIntrospector
{
	public static String DEFAULT_TAB = "parameters";
	
	public List<OProperty> getDisplayableProperties(OClass oClass);
	public List<IColumn<ODocument, String>>  getColumnsFor(OClass oClass, boolean withCheckbox, IModel<DisplayMode> modeModel);
	public List<ODocument> getNavigationPath(ODocument doc, boolean fromUpToDown);
	public ODocument getParent(ODocument doc);
	public List<String> listTabs(OClass oClass);
	public List<OProperty> listProperties(OClass oClass, String tab, Boolean extended);
	public List<OProperty> listProperties(OClass oClass, Predicate<OProperty>... predicates);
	public SortableDataProvider<ODocument, String> prepareDataProviderForProperty(OProperty property, IModel<ODocument> documentModel);
	public void defineDefaultSorting(SortableDataProvider<ODocument, String> provider, OClass oClass);
	public OProperty getNameProperty(OClass oClass);
	public String getDocumentName(ODocument doc);
	public String getDocumentName(ODocument doc, OProperty nameProperty);
	public OProperty virtualizeField(ODocument doc, String field);
	public OQueryDataProvider<ODocument> getDataProviderForGenericSearch(OClass oClass, IModel<String> queryModel);
}
