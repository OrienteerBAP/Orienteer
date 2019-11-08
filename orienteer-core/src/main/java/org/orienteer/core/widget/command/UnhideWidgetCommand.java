package org.orienteer.core.widget.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardContainer;
import org.orienteer.core.widget.command.modal.UnhideWidgetDialog;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command for {@link DashboardPanel} to unhide a widget
 *
 * @param <T> the type of main object for a {@link DashboardPanel}
 */
@OMethod(order=900+70,filters={
		@OFilter(fClass = PlaceFilter.class, fData = "DASHBOARD_SETTINGS"),
})
public class UnhideWidgetCommand<T> extends AbstractModalWindowCommand<ODocument> {
	private static final long serialVersionUID = 1L;
	
	public UnhideWidgetCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.unhide", dashboardDocumentModel);
		setIcon(FAIconType.plus);
	}

	@Override
	protected void initializeContent(final ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.unhide"));
		modal.setContent(new UnhideWidgetDialog<T>(modal.getContentId()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSelectWidget(AbstractWidget<T> widget,
					Optional<AjaxRequestTarget> targetOptional) {
				targetOptional.ifPresent(modal::close);
				widget.setHidden(false);
				DashboardPanel<T> dashboard = getDashboardPanel();
				targetOptional.ifPresent(target -> dashboard.getDashboardSupport().ajaxAddWidget(widget, target));
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