package ru.ydn.orienteer.components.structuretable;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public abstract class StructureTable<T> extends GenericPanel<List<? extends T>> 
{
	private static final String LABEL_CELL_ID = "label";
	private static final String VALUE_CELL_ID = "value";
	
	private final Caption caption;
	private final ToolbarsContainer topToolbars;

	private final ToolbarsContainer bottomToolbars;
	
	private ListView<T> listView;
	private long toolbarIdCounter;
	
	public StructureTable(String id) {
		this(id, (IModel<List<? extends T>>)null);
	}
	
	public StructureTable(String id, List<? extends T> list) {
		this(id, Model.ofList(list));
	}

	public StructureTable(String id, IModel<List<? extends T>> model) {
		super(id, model);
		setOutputMarkupPlaceholderTag(true);
		caption = new Caption("caption", getCaptionModel());
		topToolbars = new ToolbarsContainer("topToolbars");
		bottomToolbars = new ToolbarsContainer("bottomToolbars");
		add(caption, topToolbars, bottomToolbars);
		this.listView = new ListView<T>("rows", getModel()) {

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
		listView.setReuseItems(true);
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
	
	protected IModel<String> getCaptionModel()
	{
		return null;
	}
	
	public boolean getReuseItems()
	{
		return listView.getReuseItems();
	}
	
	String newToolbarId()
	{
		toolbarIdCounter++;
		return String.valueOf(toolbarIdCounter).intern();
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "table");
		tag.append("class", "table table-condensed", " ");
		super.onComponentTag(tag);
	}
	
	public void addBottomToolbar(final AbstractStructureTableToolbar<T> toolbar)
	{
		addToolbar(toolbar, bottomToolbars);
	}

	public void addTopToolbar(final AbstractStructureTableToolbar<T> toolbar)
	{
		addToolbar(toolbar, topToolbars);
	}
	
	private void addToolbar(final AbstractStructureTableToolbar<T> toolbar, final ToolbarsContainer container)
	{
		Args.notNull(toolbar, "toolbar");

		container.getRepeatingView().add(toolbar);
	}
	
	private static class ToolbarsContainer extends WebMarkupContainer
	{
		private static final long serialVersionUID = 1L;

		private final RepeatingView toolbars;

		/**
		 * Constructor
		 * 
		 * @param id
		 */
		private ToolbarsContainer(final String id)
		{
			super(id);
			toolbars = new RepeatingView("toolbars");
			add(toolbars);
		}

		public RepeatingView getRepeatingView()
		{
			return toolbars;
		}

		@Override
		public void onConfigure()
		{
			super.onConfigure();

			toolbars.configure();

			Boolean visible = toolbars.visitChildren(new IVisitor<Component, Boolean>()
			{
				@Override
				public void component(Component object, IVisit<Boolean> visit)
				{
					object.configure();
					if (object.isVisible())
					{
						visit.stop(Boolean.TRUE);
					}
					else
					{
						visit.dontGoDeeper();
					}
				}
			});
			if (visible == null)
			{
				visible = false;
			}
			setVisible(visible);
		}
	}

	/**
	 * A caption for the table. It renders itself only if {@link DataTable#getCaptionModel()} has
	 * non-empty value.
	 */
	private static class Caption extends Label
	{
		/**
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param id
		 *            the component id
		 * @param model
		 *            the caption model
		 */
		public Caption(String id, IModel<String> model)
		{
			super(id, model);
		}

		@Override
		protected void onConfigure()
		{
			setRenderBodyOnly(Strings.isEmpty(getDefaultModelObjectAsString()));

			super.onConfigure();
		}

		@Override
		protected IModel<String> initModel()
		{
			// don't try to find the model in the parent
			return null;
		}
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof AjaxRequestTarget)
		{
			((AjaxRequestTarget)event.getPayload()).add(this);
		}
	}
	
}
