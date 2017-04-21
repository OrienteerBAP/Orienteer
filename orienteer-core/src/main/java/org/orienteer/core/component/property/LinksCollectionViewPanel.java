package org.orienteer.core.component.property;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.orienteer.core.component.table.OEntityColumn;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.service.IOClassIntrospector;
import ru.ydn.wicket.wicketorientdb.model.DynamicPropertyValueModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		GenericTablePanel<ODocument> tablePanel = new GenericTablePanel<ODocument>("tablePanel", columns, provider, 10);
		OrienteerDataTable<ODocument, String> table = tablePanel.getDataTable();
		table.getHeadersToolbar().setVisibilityAllowed(false);
		table.getNoRecordsToolbar().setVisibilityAllowed(false);
		add(tablePanel);
	}

}
