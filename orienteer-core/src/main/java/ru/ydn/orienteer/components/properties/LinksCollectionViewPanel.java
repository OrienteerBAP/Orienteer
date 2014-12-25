package ru.ydn.orienteer.components.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.orienteer.CustomAttributes;
import ru.ydn.orienteer.components.ODocumentPageLink;
import ru.ydn.orienteer.components.commands.CreateODocumentCommand;
import ru.ydn.orienteer.components.commands.DeleteODocumentCommand;
import ru.ydn.orienteer.components.commands.ReleaseODocumentCommand;
import ru.ydn.orienteer.components.commands.SelectODocumentCommand;
import ru.ydn.orienteer.components.table.CheckBoxColumn;
import ru.ydn.orienteer.components.table.OEntityColumn;
import ru.ydn.orienteer.components.table.OrienteerDataTable;
import ru.ydn.orienteer.model.DynamicPropertyValueModel;
import ru.ydn.orienteer.services.IOClassIntrospector;
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

public class LinksCollectionViewPanel<T extends OIdentifiable, M extends Collection<T>> extends GenericPanel<M>
{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private IOClassIntrospector oClassIntrospector;

	public LinksCollectionViewPanel(String id, IModel<ODocument> documentModel, OProperty property) {
		super(id, new DynamicPropertyValueModel<M>(documentModel, new OPropertyModel(property)));
		
		OQueryDataProvider<ODocument> provider = oClassIntrospector.prepareDataProviderForProperty(property, documentModel);
		
		List<IColumn<ODocument, String>> columns = new ArrayList<IColumn<ODocument,String>>();
		columns.add(new OEntityColumn<ODocument>(property.getLinkedClass()));
		
		OrienteerDataTable<ODocument, String> table = new OrienteerDataTable<ODocument, String>("links", columns, provider, 10);
		table.getHeadersToolbar().setVisibilityAllowed(false);
		table.getNoRecordsToolbar().setVisibilityAllowed(false);
		add(table);
	}

}
