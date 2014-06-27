package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.components.OClassPageLink;
import ru.ydn.orienteer.components.properties.OClassViewPanel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OClassColumn extends PropertyColumn<OClass, String>
{
	public OClassColumn(IModel<String> displayModel, final String propertyExpression) {
		super(displayModel, propertyExpression);
	}
	
	public OClassColumn(IModel<String> displayModel, String sortingProperty, final String propertyExpression) {
		super(displayModel, sortingProperty, propertyExpression);
	}

	@Override
	public void populateItem(Item<ICellPopulator<OClass>> cellItem,
			String componentId, IModel<OClass> rowModel) {
		cellItem.add(new OClassViewPanel(componentId, getClassModel(rowModel)));
	}
	
	public IModel<OClass> getClassModel(IModel<OClass> rowModel)
	{
		if(Strings.isEmpty(getPropertyExpression())) return rowModel;
		else return new PropertyModel<OClass>(rowModel, getPropertyExpression());
	}
}
