package org.orienteer.core.widget.command;

import java.util.Optional;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.AbstractWidget;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IDashboardManager;
import org.orienteer.core.widget.command.modal.UnhideWidgetDialog;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Command for {@link DashboardPanel} to unhide a widget
 *
 * @param <T> the type of main object for a {@link DashboardPanel}
 */
public class UnhideWidgetCommand<T> extends AbstractModalWindowCommand<ODocument> {
	
	@Inject
	private IDashboardManager dashboardManager;
	
	public UnhideWidgetCommand(String id, IModel<ODocument> dashboardDocumentModel) {
		super(id, "command.unhide", dashboardDocumentModel);
		setIcon(FAIconType.plus_circle);
		setBootstrapType(BootstrapType.PRIMARY);
		setBootstrapSize(BootstrapSize.EXTRA_SMALL);
	}

	@Override
	protected void initializeContent(final ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.unhide"));
		modal.setContent(new UnhideWidgetDialog<T>(modal.getContentId()) {

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

}