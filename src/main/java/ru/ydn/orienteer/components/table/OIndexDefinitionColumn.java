package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;

import ru.ydn.orienteer.components.properties.OIndexViewPanel;
import ru.ydn.orienteer.components.properties.OPropertyViewPanel;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OIndexDefinitionColumn<T> extends PropertyColumn<T, String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OIndexDefinitionColumn(IModel<String> displayModel, final String propertyExpression) {
		super(displayModel, propertyExpression);
	}
	
	public OIndexDefinitionColumn(IModel<String> displayModel, String sortingProperty, final String propertyExpression) {
		super(displayModel, sortingProperty, propertyExpression);
	}
	
	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem,
			String componentId, IModel<T> rowModel) {
		cellItem.add(new OIndexViewPanel(componentId, getPropertyModel(rowModel)));
	}
	
	@SuppressWarnings("unchecked")
	public IModel<OIndex<?>> getPropertyModel(IModel<T> rowModel)
	{
		if(Strings.isEmpty(getPropertyExpression())) return (IModel<OIndex<?>>)rowModel;
		else return new PropertyModel<OIndex<?>>(rowModel, getPropertyExpression());
	}
}
