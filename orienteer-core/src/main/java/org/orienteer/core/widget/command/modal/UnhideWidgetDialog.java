package org.orienteer.core.widget.command.modal;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.orienteer.core.component.command.AjaxCommand;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardManager;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.IWidgetTypesRegistry;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Dialog for modal window to unhide a widgets
 *
 * @param <T> the type of main data object for {@link DashboardPanel}
 */
public abstract class UnhideWidgetDialog<T> extends Panel {
	
	@Inject
	private IWidgetTypesRegistry registry;
	
	@Inject
	private IDashboardManager dashboardManager;
	
	public UnhideWidgetDialog(String id) {
		super(id);
		add(new ListView<String>("widgets", new PropertyModel<List<String>>(this, "hiddenWidgetIds")) {

			@Override
			protected void populateItem(final ListItem<String> item) {
				item.add(new Label("name", new LoadableDetachableModel<String>() {

					@Override
					protected String load() {
						AbstractWidget<T> widget = (AbstractWidget<T>)getDashboardPanel().getWidgetsContainer().get(item.getModelObject());
						return registry.lookupByWidgetClass((Class<? extends AbstractWidget<T>>)widget.getClass()).getId();
					}
				}));
				item.add(new AjaxCommand<T>("select", "command.unhide") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						AbstractWidget<T> widget = (AbstractWidget<T>)getDashboardPanel().getWidgetsContainer().get(item.getModelObject());
						onSelectWidget(widget, target);
					}
				});
			}
		});
	}
	
	protected abstract void onSelectWidget(AbstractWidget<T> widget, AjaxRequestTarget target);
	
	public DashboardPanel<T> getDashboardPanel() {
		DashboardPanel<T> dashboard = findParent(DashboardPanel.class);
		if(dashboard==null)
		{
			throw new WicketRuntimeException("No dashboard found for widget: "+this);
		}
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