package org.orienteer.core.component.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.component.ODocumentPageLink;
import org.orienteer.core.component.command.CreateODocumentCommand;
import org.orienteer.core.component.command.DeleteODocumentCommand;
import org.orienteer.core.component.command.ReleaseODocumentCommand;
import org.orienteer.core.component.command.SelectODocumentCommand;
import org.orienteer.core.component.table.CheckBoxColumn;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.service.IOClassIntrospector;

import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.utils.ODocumentORIDConverter;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link GenericPanel} to view list or set of links
 *
 * @param <T> the type of {@link OIdentifiable} - commonly {@link ODocument}
 * @param <M> the type of a collection
 */
public class LinksCollectionViewPanel<T extends OIdentifiable, M extends Collection<T>> extends GenericPanel<M>
{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	public LinksCollectionViewPanel(String id, IModel<ODocument> documentModel, OProperty property) {
		super(id, new DynamicPropertyValueModel<M>(documentModel, new OPropertyModel(property)));
		
		ISortableDataProvider<ODocument, String> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
		
		List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
		columns.add(new OEntityColumn(property.getLinkedClass(), DisplayMode.VIEW.asModel()));
		
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("links", columns, provider, 10);
		table.getHeadersToolbar().setVisibilityAllowed(false);
		table.getNoRecordsToolbar().setVisibilityAllowed(false);
		add(table);
	}

}
