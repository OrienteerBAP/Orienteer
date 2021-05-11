package org.orienteer.core.widget.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.event.SwitchDashboardTabEvent;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.web.AbstractWidgetPage;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardContainer;
import org.orienteer.core.widget.command.modal.AddTabDialog;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command for {@link DashboardPanel} to add new tab
 *
 * @param <T> the type of main object for a {@link DashboardPanel}
 */
@OMethod(order=900+50, bootstrap = BootstrapType.LIGHT,
	filters={
			@OFilter(fClass = PlaceFilter.class, fData = "DASHBOARD_SETTINGS"),
	})
public class AddTabCommand<T> extends AbstractModalWindowCommand<ODocument> {
	private static final long serialVersionUID = 1L;

	public AddTabCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.add.tab", dashboardDocumentModel);
		setIcon(FAIconType.plus_square);
	}

	@Override
	protected void initializeContent(final ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.add.tab"));
		modal.setContent(new AddTabDialog<T>(modal.getContentId()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onCreateTab(String name, Optional<AjaxRequestTarget> targetOptional) {
				targetOptional.ifPresent(modal::close);
				if(Strings.isEmpty(name)) {
					name = getLocalizer().getString("command.add.tab.modal.defaultname", null);
				}
				
				AbstractWidgetPage<T> page = (AbstractWidgetPage<T>)findPage();
				page.getCurrentDashboard().setModeObject(DisplayMode.VIEW); //Close action on current tab
				page.addTab(name);
				page.selectTab(name, targetOptional);
				send(page, Broadcast.DEPTH, new SwitchDashboardTabEvent(targetOptional));
				page.getCurrentDashboard().setModeObject(DisplayMode.EDIT); //Open action on new tab
				targetOptional.ifPresent(target -> target.add(page.getCurrentDashboardComponent()));
			}
		});
		modal.setAutoSize(true);
		modal.setMinimalWidth(300);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		IDashboardContainer<?> dashboardContainer = findParent(IDashboardContainer.class);
		setVisible(dashboardContainer.hasDashboard() && dashboardContainer.getDashboardModeObject().canModify());
	}
}
