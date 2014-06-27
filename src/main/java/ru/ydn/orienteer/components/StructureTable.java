package ru.ydn.orienteer.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class StructureTable<T> extends GenericPanel<List<? extends T>> 
{
	private static final String LABEL_CELL_ID = "label";
	private static final String VALUE_CELL_ID = "value";
	
	private ListView<T> listView;
	
	public StructureTable(String id) {
		super(id);
		initialize();
	}
	
	public StructureTable(String id, List<? extends T> list) {
		this(id, Model.ofList(list));
	}

	public StructureTable(String id, IModel<List<? extends T>> model) {
		super(id, model);
		initialize();
	}
	
	protected void initialize()
	{
		listView = new ListView<T>("rows", getModel()) {

			@Override
			protected void populateItem(ListItem<T> item) {
				IModel<T> rowModel = item.getModel();
				Component label = getLabelComponent(LABEL_CELL_ID, rowModel);
				if(!LABEL_CELL_ID.equals(label.getId())) throw new WicketRuntimeException("Wrong component id '"+label.getId()+"'. Should be '"+LABEL_CELL_ID+"'.");
				Component value = getValueComponent(VALUE_CELL_ID, rowModel);
				if(!VALUE_CELL_ID.equals(value.getId())) throw new WicketRuntimeException("Wrong component id '"+value.getId()+"'. Should be '"+VALUE_CELL_ID+"'.");
				item.add(label, value);
			}
		};
		add(listView);
	}
	
	protected abstract Component getValueComponent(String id, IModel<T> rowModel);
	
	protected Component getLabelComponent(String id, IModel<T> rowModel)
	{
		return new Label(id, getLabelModel(rowModel));
	}
	
	protected IModel<?> getLabelModel(IModel<T> rowModel)
	{
		return rowModel;
	}
	
	public StructureTable<T> setReuseItems(boolean reuseItems)
	{
		listView.setReuseItems(reuseItems);
		return this;
	}
	
	public boolean getReuseItems()
	{
		return listView.getReuseItems();
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "table");
		tag.append("class", "table", " ");
		super.onComponentTag(tag);
	}
	
	
}
