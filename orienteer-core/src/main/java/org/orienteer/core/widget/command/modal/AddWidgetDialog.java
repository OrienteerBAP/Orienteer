package org.orienteer.core.widget.command.modal;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.OClassPageLink;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.table.OrienteerDataTable;
import org.orienteer.core.web.BrowseOClassPage;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.IWidgetTypesRegistry;

import ru.ydn.wicket.wicketorientdb.model.AbstractJavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.JavaSortableDataProvider;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Dialog for modal window to select and add new type of widget
 *
 * @param <T> the type of main data object for {@link DashboardPanel}
 */
public abstract class AddWidgetDialog<T> extends Panel {
	
	@Inject
	private IWidgetTypesRegistry registry;

	public AddWidgetDialog(String id) {
		super(id);
		List<IColumn<IWidgetType<T>, String>> columns = new ArrayList<IColumn<IWidgetType<T>,String>>();
		columns.add(new AbstractColumn<IWidgetType<T>, String>(new ResourceModel("widget.id")) {

			@Override
			public void populateItem(
					Item<ICellPopulator<IWidgetType<T>>> cellItem,
					String componentId, IModel<IWidgetType<T>> rowModel) {
				cellItem.add(new Label(componentId, new SimpleNamingModel<String>("widget", new PropertyModel<String>(rowModel, "id"))));
			}
		});
		columns.add(new AbstractColumn<IWidgetType<T>, String>(null) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<IWidgetType<T>>> cellItem,
					String componentId, final IModel<IWidgetType<T>> rowModel) {
				cellItem.add(new AjaxCommand<T>(componentId, "command.add.widget") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						onSelectWidgetType(rowModel.getObject(), target);
					}
				}.setIcon(FAIconType.play_circle_o).setBootstrapType(BootstrapType.INFO));
				
			}
		});
		ISortableDataProvider<IWidgetType<T>, String> provider 
			= new JavaSortableDataProvider<IWidgetType<T>, String>(new PropertyModel<List<IWidgetType<T>>>(this, "availableWidgetTypes"));
		
		add(new OrienteerDataTable<IWidgetType<T>, String>("table", columns, provider, 20));
	}
	
	protected abstract void onSelectWidgetType(IWidgetType<T> type, AjaxRequestTarget target);
	
	public DashboardPanel<T> getDashboardPanel() {
		DashboardPanel<T> dashboard = findParent(DashboardPanel.class);
		if(dashboard==null)
		{
			throw new WicketRuntimeException("No dashboard found for widget: "+this);
		}
		return dashboard;
	}
	
	public List<IWidgetType<T>> getAvailableWidgetTypes() {
		DashboardPanel<T> dashboard = getDashboardPanel();
		return registry.lookupByDomainAndTab(dashboard.getDomain(), dashboard.getTab(), dashboard.getWidgetsFilter());
	}
	
}
