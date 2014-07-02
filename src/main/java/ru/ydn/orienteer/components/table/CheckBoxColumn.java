package ru.ydn.orienteer.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;

import ru.ydn.orienteer.components.properties.BooleanEditPanel;
import ru.ydn.wicket.wicketorientdb.utils.GetObjectFunction;

public class CheckBoxColumn<T, S> extends AbstractColumn<T, S>
{
	private List<Serializable> selected = new ArrayList<Serializable>();
	private Converter<T, Serializable> converterToPK;

	public CheckBoxColumn(IModel<String> displayModel, S sortProperty, Converter<T, Serializable> converterToPK) {
		super(displayModel, sortProperty);
		this.converterToPK = converterToPK;
	}

	public CheckBoxColumn(IModel<String> displayModel) {
		super(displayModel);
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem,
			String componentId, IModel<T> rowModel) {
		cellItem.add(new BooleanEditPanel(componentId, getCheckBoxModel(rowModel)));
	}
	
	protected AbstractCheckBoxModel getCheckBoxModel(final IModel<T> rowModel)
	{
		return new AbstractCheckBoxModel() {
			
			@Override
			public void unselect() {
				selected.remove(converterToPK.convert(rowModel.getObject()));
			}
			
			@Override
			public void select() {
				selected.add(converterToPK.convert(rowModel.getObject()));
			}
			
			@Override
			public boolean isSelected() {
				return selected.contains(converterToPK.convert(rowModel.getObject()));
			}
		};
	}
	
	public List<T> getSelected()
	{
		return Lists.transform(selected, converterToPK.reverse());
	}
	
}
