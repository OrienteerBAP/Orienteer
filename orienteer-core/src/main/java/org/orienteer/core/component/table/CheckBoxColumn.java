package org.orienteer.core.component.table;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.core.component.property.BooleanEditPanel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link IColumn} to display checkboxes and aggregate selected options
 *
 * @param <T> the type of the object that will be rendered in this column's cells
 * @param <P> the type of a primary key to be used for aggregation
 * @param <S> the type of the sort property
 */
public class CheckBoxColumn<T, P extends Serializable, S> extends AbstractColumn<T, S> {
	private static final JavaScriptResourceReference SELECT_ALL_JS = new JavaScriptResourceReference(CheckBoxColumn.class, "select-all.js");

	private static final long serialVersionUID = 1L;
	private List<P> selected = new ArrayList<P>();
	private Converter<T, P> converterToPK;

	public CheckBoxColumn(Converter<T, P> converterToPK) {
		super(null);
		this.converterToPK = converterToPK;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem,
			String componentId, IModel<T> rowModel) {
		cellItem.add(new BooleanEditPanel(componentId, getCheckBoxModel(rowModel)));
	}
	
	@Override
	public Component getHeader(String componentId) {
		return new BooleanEditPanel(componentId, Model.of(false))
		{
			@Override
			protected Component newCheckbox(String componentId) {
				return super.newCheckbox(componentId).setOutputMarkupId(true);
			}
			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				response.render(JavaScriptHeaderItem.forReference(SELECT_ALL_JS, "select-all"));
				String script = "installSelectAll('"+getCheckbox().getMarkupId()+"');";
				response.render(OnDomReadyHeaderItem.forScript(script));
			}
		};
	}
	
	protected AbstractCheckBoxModel getCheckBoxModel(final IModel<T> rowModel)
	{
		return new AbstractCheckBoxModel() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void unselect() {
				CheckBoxColumn.this.unselect(rowModel.getObject());
			}
			
			@Override
			public void select() {
				CheckBoxColumn.this.select(rowModel.getObject());
			}
			
			@Override
			public boolean isSelected() {
				return CheckBoxColumn.this.isSelected(rowModel.getObject());
			}
		};
	}
	
	public void resetSelection()
	{
		selected.clear();
	}
	
	public void unselect(T object) {
		selected.remove(converterToPK.convert(object));
	}
	
	public void select(T object) {
		selected.add(converterToPK.convert(object));
	}
	
	public boolean isSelected(T object) {
		return selected.contains(converterToPK.convert(object));
	}
	
	public List<T> getSelected()
	{
		return Lists.transform(selected, converterToPK.reverse());
	}

	@Override
	public String getCssClass() {
		return "checkbox-column";
	}
}