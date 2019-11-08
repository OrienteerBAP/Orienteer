package org.orienteer.core.web;

import com.google.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.TabbedPanel;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.event.SwitchDashboardTabEvent;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardManager;
import org.orienteer.core.widget.IWidgetFilter;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract class for all pages that use widgets approach
 *
 * @param <T> the type of a main object for this page
 */
public abstract class AbstractWidgetPage<T> extends OrienteerBasePage<T> {
	
	private class DashboardTab implements ITab {
		
		private String tab;
		private IModel<String> titleModel;
		
		private DashboardPanel dashboard;
		
		public DashboardTab(String tab) {
			this(tab, newTabNameModel(tab));
		}
		
		public DashboardTab(String tab, IModel<String> titleModel) {
			this.tab = tab;
			this.titleModel = titleModel;
		}

		@Override
		public IModel<String> getTitle() {
			return titleModel;
		}

		@Override
		public WebMarkupContainer getPanel(String containerId) {
			if(dashboard==null) {
				dashboard = newDashboard(containerId, getDomain(), tab, getModel(), getWidgetsFilter());
			}
			return dashboard;
		}

		@Override
		public boolean isVisible() {
			return true;
		}
		
	}
	
	@Inject
	protected IDashboardManager dashboardManager;
	
	protected TabbedPanel<DashboardTab> tabbedPanel;
	
	private IWidgetFilter<T> widgetsFilter;
	
	public AbstractWidgetPage() {
		super();
	}

	public AbstractWidgetPage(IModel<T> model) {
		super(model);
	}

	public AbstractWidgetPage(PageParameters parameters) {
		super(parameters);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		add(tabbedPanel = new TabbedPanel<DashboardTab>("dashboardTabs", getDashboardTabs()){
			@Override
			protected void onLinkClick(AjaxRequestTarget target) {
				super.onLinkClick(target);
				send(AbstractWidgetPage.this, Broadcast.DEPTH, new SwitchDashboardTabEvent(Optional.ofNullable(target)));
			}
		});
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		switchToDefaultTab();
	}
	
	/**
	 * Set initial tab to default one.
	 * @return true of switch was actually performed and false otherwise
	 */
	protected boolean switchToDefaultTab() {
		PageParameters parameters = getPageParameters();
		String tab = parameters.get("tab").toOptionalString();
		if(Strings.isEmpty(tab)) return false;
		else return selectTab(tab);
	}
	
	public boolean isHideTabsIfSingle() {
		return tabbedPanel.isHideIfSingle();
	}
	
	public AbstractWidgetPage<T> setHideTabsIfSingle(boolean hideTabsIfSingle) {
		tabbedPanel.setHideIfSingle(hideTabsIfSingle);
		return this;
	}
	
	protected IModel<String> newTabNameModel(String tabName)
	{
		return new SimpleNamingModel<String>("tab", tabName);
	}
	
	protected List<DashboardTab> getDashboardTabs() {
		List<String> tabs = getTabs();
		List<DashboardTab> ret = new ArrayList<DashboardTab>(tabs.size());
		for (String tab : tabs) {
			ret.add(new DashboardTab(tab));
		}
		
		return ret;
	}
	
	public List<String> getTabs() {
		return dashboardManager.listTabs(getDomain(), getWidgetsFilter(), getModelObject());
	}
	
	/**
	 * Select tab
	 * @param tab the name of tab to select
	 * @return true if tab was switched and false if there is no such tab;
	 */
	public boolean selectTab(String tab) {
		if(tab==null) return false;
		List<DashboardTab> tabs = tabbedPanel.getTabs();
		for(int i=0; i<tabs.size();i++) {
			if(tab.equals(tabs.get(i).tab)) {
				tabbedPanel.setSelectedTab(i);
				return true;
			}
		}
		return false;
	}
	
	protected DashboardPanel<T> newDashboard(String id, String domain, String tab, IModel<T> model, IWidgetFilter<T> widgetsFilter) {
		return new DashboardPanel<T>(id, domain, tab, model, widgetsFilter);
	}
	
	public IWidgetFilter<T> getWidgetsFilter() {
		return widgetsFilter;
	}

	public void setWidgetsFilter(IWidgetFilter<T> widgetsFilter) {
		this.widgetsFilter = widgetsFilter;
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof ActionPerformedEvent && Broadcast.BUBBLE.equals(event.getType())) {
			send(this, Broadcast.BREADTH, event.getPayload());
		}
	}
	
	@Override
	public IModel<String> getTitleModel() {
		return new ResourceModel(getDomain());
	}

	public abstract String getDomain();
	
	
}
