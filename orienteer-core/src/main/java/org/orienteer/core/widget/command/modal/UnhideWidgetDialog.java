package org.orienteer.core.widget.command.modal;

import com.google.inject.Inject;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.table.component.GenericTablePanel;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardContainer;
import org.orienteer.core.widget.IWidgetTypesRegistry;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for modal window to unhide a widgets
 *
 * @param <T> the type of main data object for {@link DashboardPanel}
 */
public abstract class UnhideWidgetDialog<T> extends Panel {
	
	@Inject
	private IWidgetTypesRegistry registry;
	
	public UnhideWidgetDialog(String id) {
		super(id);
		
		List<IColumn<String, String>> columns = new ArrayList<IColumn<String,String>>();
		columns.add(new AbstractColumn<String, String>(new ResourceModel("widget.id")) {

			@Override
			public void populateItem(Item<ICellPopulator<String>> cellItem,
					String componentId, final IModel<String> rowModel) {
				cellItem.add(new Label(componentId, new SimpleNamingModel<String>("widget", new AbstractReadOnlyModel<String>() {

					@Override
					public String getObject() {
						AbstractWidget<T> widget = (AbstractWidget<T>)getDashboardPanel().getWidgetsContainer().get(rowModel.getObject());
						return registry.lookupByWidgetClass((Class<? extends AbstractWidget<T>>)widget.getClass()).getId();
					}
				})));
			}
		});
		columns.add(new AbstractColumn<String, String>(null) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<String>> cellItem,
					String componentId, final IModel<String> rowModel) {
				cellItem.add(new AjaxCommand<T>(componentId, "command.unhide") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						AbstractWidget<T> widget = (AbstractWidget<T>)getDashboardPanel().getWidgetsContainer().get(rowModel.getObject());
						onSelectWidget(widget, target);
					}
				}.setIcon(FAIconType.play_circle_o).setBootstrapType(BootstrapType.INFO));
				
			}
		});
		ISortableDataProvider<String, String> provider 
			= new JavaSortableDataProvider<String, String>(new PropertyModel<List<String>>(this, "hiddenWidgetIds"));
		GenericTablePanel<String> tablePanel = new GenericTablePanel<String>("tablePanel", columns, provider, 20);
		add(tablePanel);
	}
	
	protected abstract void onSelectWidget(AbstractWidget<T> widget, AjaxRequestTarget target);
	
	public DashboardPanel<T> getDashboardPanel() {
		IDashboardContainer container = findParent(IDashboardContainer.class);
		if(container.getCurrentDashboard()==null)
		{
			throw new WicketRuntimeException("No dashboard found for widget: "+this);
		}
		DashboardPanel<T> dashboard = container.getCurrentDashboard().getSelfComponent();
		return dashboard;
	}
	
	public List<String> getHiddenWidgetIds() {
		DashboardPanel<T> dashboard = getDashboardPanel();
		List<String> ids = new ArrayList<String>();
		for(AbstractWidget<T> widget : dashboard.getWidgets()) {
			if(widget.isHidden()) ids.add(widget.getId());
		}
		return ids;
	}
	
}