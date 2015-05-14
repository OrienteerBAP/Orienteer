package org.orienteer.core.widget.command;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.ResourceModel;
import org.orienteer.core.component.BootstrapSize;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.command.AbstractModalWindowCommand;
import org.orienteer.core.component.command.modal.ImportDialogPanel;
import org.orienteer.core.widget.DashboardPanel;
import org.orienteer.core.widget.IWidgetType;
import org.orienteer.core.widget.command.modal.AddWidgetDialog;

/**
 * Command for {@link DashboardPanel} to add new widget
 *
 * @param <T> the type of main object for a {@link DashboardPanel}
 */
public class AddWidgetCommand<T> extends AbstractModalWindowCommand<T> {

	public AddWidgetCommand(String id) {
		super(id, "command.add.widget");
		setIcon(FAIconType.plus_circle);
		setBootstrapType(BootstrapType.SUCCESS);
		setBootstrapSize(BootstrapSize.EXTRA_SMALL);
	}

	@Override
	protected void initializeContent(final ModalWindow modal) {
		modal.setTitle(new ResourceModel("command.add.widget"));
		modal.setContent(new AddWidgetDialog<T>(modal.getContentId()) {

			@Override
			protected void onSelectWidgetType(IWidgetType<T> type,
					AjaxRequestTarget target) {
				DashboardPanel<T> dashboard = getDashboardPanel();
				dashboard.addWidget(type);
				modal.close(target);
				target.add(dashboard);
			}
		});
	}

}
