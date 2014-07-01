package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.LinkViewPanel;
import ru.ydn.orienteer.schema.SchemaHelper;
import ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;

import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OEntityColumn extends AbstractColumn<ODocument, String>
{
	public OEntityColumn(OClass oClass)
	{
		this(new OClassNamingModel(oClass), oClass);
	}
	public OEntityColumn(IModel<String> displayModel, OClass oClass) {
		super(displayModel, SchemaHelper.resolveNameProperty(oClass));
	}
	public OEntityColumn(IModel<String> displayModel, String oClass) {
		super(displayModel, SchemaHelper.resolveNameProperty(oClass));
	}
	
	public String getNameProperty()
	{
		return getSortProperty();
	}

	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
			String componentId, IModel<ODocument> rowModel) {
		cellItem.add(new LinkViewPanel(componentId, rowModel));
	}

}
