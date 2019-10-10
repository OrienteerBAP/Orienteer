package org.orienteer.core.component.structuretable;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ILabelProvider;
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
import org.orienteer.core.component.ITooltipProvider;

import ru.ydn.wicket.wicketorientdb.behavior.SyncVisibilityBehaviour;

/**
 * Panel which displays parameters of a specified object
 *
 * @param <T> the type of main object for this table
 * @param <C> the type of criterias to be used for this table
 */
public abstract class StructureTable<T, C> extends GenericPanel<T> 
{
	private static final long serialVersionUID = 1L;
	private static final String LABEL_CELL_ID = "label";
	private static final String VALUE_CELL_ID = "value";
	
	private final Caption caption;
	private final ToolbarsContainer topToolbars;

	private final ToolbarsContainer bottomToolbars;
	
	private ListView<C> listView;
	private long toolbarIdCounter;
	private IModel<? extends List<? extends C>> criteriesModel;
	
	public StructureTable(String id, IModel<T> model, List<C> list) {
		this(id, model, Model.ofList(list));
	}

	public StructureTable(String id, IModel<T> model, IModel<? extends List<C>> criteriesModel) {
		super(id, model);
		this.criteriesModel = criteriesModel;
		setOutputMarkupPlaceholderTag(true);
		caption = new Caption("caption", Model.of(""));
		topToolbars = new ToolbarsContainer("topToolbars");
		bottomToolbars = new ToolbarsContainer("bottomToolbars");
		add(caption, topToolbars, bottomToolbars);
		this.listView = new ListView<C>("rows", criteriesModel) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<C> item) {
				IModel<C> rowModel = item.getModel();
				Component value = getValueComponent(VALUE_CELL_ID, rowModel);
				if(!VALUE_CELL_ID.equals(value.getId())) throw new WicketRuntimeException("Wrong component id '"+value.getId()+"'. Should be '"+VALUE_CELL_ID+"'.");
				item.add(new SyncVisibilityBehaviour(value));
				Component label = getLabelComponent(LABEL_CELL_ID, rowModel, getLabelModel(value, rowModel));
				if(!LABEL_CELL_ID.equals(label.getId())) throw new WicketRuntimeException("Wrong component id '"+label.getId()+"'. Should be '"+LABEL_CELL_ID+"'.");
				label.add(new AttributeModifier("title", getTooltipModel(value, rowModel)));
				item.add(label, value);
			}

		};
		listView.setReuseItems(true);
		add(listView);
	}
	
	protected abstract Component getValueComponent(String id, IModel<C> rowModel);
	
	protected Component getLabelComponent(String id, IModel<C> rowModel, IModel<?> labelModel)
	{
		return new Label(id, labelModel);
	}
	
	protected IModel<?> getLabelModel(Component resolvedComponent, IModel<C> rowModel)
	{
		if(resolvedComponent instanceof ILabelProvider<?>)
		{
			return ((ILabelProvider<?>)resolvedComponent).getLabel();
		}
		else
		{
			return rowModel;
		}
	}
	
	protected IModel<?> getTooltipModel(Component resolvedComponent, IModel<C> rowModel)
	{
		if(resolvedComponent instanceof ITooltipProvider<?>)
		{
			return ((ITooltipProvider<?>)resolvedComponent).getTooltip();
		}
		else
		{
			return null;
		}
	}
	
	public StructureTable<T, C> setReuseItems(boolean reuseItems)
	{
		listView.setReuseItems(reuseItems);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public IModel<String> getCaptionModel()
	{
		return (IModel<String>)caption.getDefaultModel();
	}
	
	public StructureTable<T, C> setCaptionModel(IModel<String> captionModel)
	{
		caption.setDefaultModel(captionModel);
		return this;
	}
	
	public IModel<? extends List<? extends C>> getCriteriesModel() {
		return criteriesModel;
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
		tag.append("class", "table table-sm structure-table", " ");
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
	
	@Override
	public void detachModels() {
		super.detachModels();
		if(criteriesModel!=null) criteriesModel.detach();
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
	 * A caption for the table. It renders itself only if {@link DataTable} caption has
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
	
}
