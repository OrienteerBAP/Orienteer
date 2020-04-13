package org.orienteer.core.component;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.event.ActionPerformedEvent;
import org.orienteer.core.event.SwitchDashboardTabEvent;
import org.orienteer.core.method.MethodPlace;
import org.orienteer.core.method.OMethodsManager;
import org.orienteer.core.module.OWidgetsModule;
import org.orienteer.core.widget.IDashboardContainer;
import org.orienteer.core.widget.command.ConfigureDashboardCommand;
import org.orienteer.core.widget.command.KeepUnsavedDashboardCommand;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

import java.util.Optional;

/**
 * 
 * Default breadcrumb menu
 *
 */
@RequiredOrientResource(value = OSecurityHelper.CLASS, specific = OWidgetsModule.OCLASS_DASHBOARD, permissions = OrientPermission.UPDATE)
public class DefaultPageHeaderMenu extends GenericPanel<ODocument> implements ICommandsSupportComponent<ODocument> {
	private static final long serialVersionUID = 1L;
	private RepeatingView commands;

	public DefaultPageHeaderMenu(String id) {
		super(id,new ODocumentModel());

		add(commands = new RepeatingView("commands"));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		getParent().setOutputMarkupId(true);
		OMethodsManager.get().populate(this, MethodPlace.DASHBOARD_SETTINGS, getModel());
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof SwitchDashboardTabEvent) {
			((SwitchDashboardTabEvent)event.getPayload()).getTarget().ifPresent(target -> target.add(getParent()));
		}
		else if(event.getPayload() instanceof ActionPerformedEvent) {
			ActionPerformedEvent<?> action = (ActionPerformedEvent<?>) event.getPayload();
			Command<?> command = action.getCommand();
			if(command.isChangingModel() || command.isChangingDisplayMode()) {
				action.getTarget().ifPresent(target -> target.add(getParent()));
			}
		}
	}

	@Override
	public DefaultPageHeaderMenu addCommand(Command<ODocument> command) {
		commands.add(command);
		return this;
	}

	@Override
	public DefaultPageHeaderMenu removeCommand(Command<ODocument> command) {
		commands.remove(command);
		return this;
	}

	@Override
	public String newCommandId() {
		return commands.newChildId();
	}
	
}
