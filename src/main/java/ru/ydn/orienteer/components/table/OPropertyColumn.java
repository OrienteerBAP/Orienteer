package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.components.properties.MetaPanel;
import ru.ydn.wicket.wicketorientdb.model.AutoResourceModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OPropertyColumn extends AbstractColumn<ODocument, String>
{
	private String property;
	
	public OPropertyColumn(OProperty property)
	{
		this(new AutoResourceModel(property), property.getName());
	}
	
	public OPropertyColumn(IModel<String> displayModel, String property) {
		super(displayModel, property);
		this.property = property;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void populateItem(Item<ICellPopulator<ODocument>> cellItem,
			String componentId, IModel<ODocument> rowModel) {
		IModel<OProperty> propertyModel = new OPropertyModel(new OClassModel((IModel<ODocument>)rowModel), property);
		cellItem.add(new MetaPanel<Object>(componentId, rowModel, propertyModel, DisplayMode.VIEW.asModel()));
	}


}
