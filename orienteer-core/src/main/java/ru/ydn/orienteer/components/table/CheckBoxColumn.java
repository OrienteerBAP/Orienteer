package ru.ydn.orienteer.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.model.AbstractCheckBoxModel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;

import ru.ydn.orienteer.components.properties.BooleanEditPanel;

public class CheckBoxColumn<T, PK extends Serializable, S> extends AbstractColumn<T, S>
{
	private class CheckboxPanel extends BooleanEditPanel
	{
		public CheckboxPanel(String id, IModel<T> rowModel)
		{
			super(id, getCheckBoxModel(rowModel));
		}
		
		public CheckBoxColumn<T, PK, S> getColumn()
		{
			return CheckBoxColumn.this;
		}
	}
	private static final long serialVersionUID = 1L;
	private List<PK> selected = new ArrayList<PK>();
	private Converter<T, PK> converterToPK;
	private Boolean checkedAll;
	private BooleanEditPanel checkAllBox;

	public CheckBoxColumn(Converter<T, PK> converterToPK) {
		super(null);
		this.converterToPK = converterToPK;
	}

	@Override
	public void populateItem(Item<ICellPopulator<T>> cellItem,
			String componentId, IModel<T> rowModel) {
		cellItem.add(new CheckboxPanel(componentId, rowModel));
	}
	
	protected AbstractCheckBoxModel getCheckBoxModel(final IModel<T> rowModel)
	{
		return new AbstractCheckBoxModel() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void unselect() {
				CheckBoxColumn.this.unselect(rowModel.getObject());
				checkedAll = false;
			}
			
			@Override
			public void select() {
				CheckBoxColumn.this.select(rowModel.getObject());
				checkedAll = null;
			}
			
			@Override
			public boolean isSelected() {
				return CheckBoxColumn.this.isSelected(rowModel.getObject());
			}
		};
	}
	
	@Override
	public Component getHeader(String componentId) {
		IModel<Boolean> checkboxModel = new AbstractCheckBoxModel() {
			
			@Override
			public void unselect() {
				resetSelection();
				checkedAll=false;
			}
			
			@Override
			public void select() {
				checkAllBox.findParent(DataTable.class).visitChildren(CheckboxPanel.class, new IVisitor<CheckboxPanel, Void>() {

					@SuppressWarnings("unchecked")
					@Override
					public void component(
							CheckBoxColumn<T, PK, S>.CheckboxPanel checkbox,
							IVisit<Void> visit) {
						CheckBoxColumn.this.select((T)checkbox.getParent().getParent().getParent().getDefaultModelObject());
					}
				});
				checkedAll=true;
			}
			
			@Override
			public boolean isSelected() {
				if(checkedAll==null)
				{
					checkedAll = checkAllBox.findParent(DataTable.class).visitChildren(CheckboxPanel.class, new IVisitor<CheckboxPanel, Boolean>() {
	
						@Override
						public void component(
								CheckBoxColumn<T, PK, S>.CheckboxPanel checkbox,
								IVisit<Boolean> visit) {
							if(checkbox.getColumn().equals(CheckBoxColumn.this))
							{
								Boolean checked = checkbox.getModelObject();
								if(checked==null || !checked)
								{
									visit.stop(false);
								}
							}
							else
							{
								visit.dontGoDeeper();
							}
						}
					});
					if(checkedAll==null) checkedAll=true;
				}
				return checkedAll;
			}
		};
		
		checkAllBox =  new BooleanEditPanel(componentId, checkboxModel)
		{
			@Override
			protected Component newCheckbox(String componentId) {
				return super.newCheckbox(componentId).add(new OnChangeAjaxBehavior() {
					
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						checkAllBox.send(checkAllBox, Broadcast.BUBBLE, target);
					}
				});
			}
		};
		return checkAllBox;
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
