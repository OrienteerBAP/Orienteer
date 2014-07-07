package ru.ydn.orienteer.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import ru.ydn.orienteer.components.properties.AbstractMetaPanel;

public abstract class AbstractMetaColumn<T, C, S> extends AbstractColumn<T, S>
{
	private IModel<C> criteryModel;
	
	public AbstractMetaColumn(IModel<String> displayModel, IModel<C> criteryModel) {
		super(displayModel);
		this.criteryModel = criteryModel;
	}
	
	public AbstractMetaColumn(IModel<String> displayModel, final S sortProperty, IModel<C> criteryModel) {
		super(displayModel, sortProperty);
		this.criteryModel = criteryModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
		cellItem.add(newMetaPanel(componentId, criteryModel, rowModel));
	}
	
	protected abstract <V> AbstractMetaPanel<T, C, V> newMetaPanel(String componentId, IModel<C> criteryModel, IModel<T> rowModel);

	@Override
	public void detach() {
		super.detach();
		if(criteryModel!=null)
		{
			criteryModel.detach();
		}
	}

	

}