package org.orienteer.core.widget.command.modal;

import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.IWidgetTypesRegistry;

import com.google.inject.Inject;

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
		add(new ListView<IWidgetType<T>>("types", new PropertyModel<List<IWidgetType<T>>>(this, "availableWidgetTypes")) {

			@Override
			protected void populateItem(final ListItem<IWidgetType<T>> item) {
				item.add(new Label("name", new PropertyModel<String>(item.getModel(), "id")));
				item.add(new AjaxCommand<T>("select", "command.add.widget") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						onSelectWidgetType(item.getModelObject(), target);
					}
				});
			}
		});
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
		return registry.lookupByDomainAndTab(dashboard.getDomain(), dashboard.getTab());
	}
	
}
