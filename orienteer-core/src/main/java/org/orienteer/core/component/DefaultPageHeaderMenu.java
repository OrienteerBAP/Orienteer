package org.orienteer.core.component;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.orienteer.core.component.command.Command;
import org.orienteer.core.component.property.DisplayMode;
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
	private Component configure;
	private Component close;
	private RepeatingView commands;

	public DefaultPageHeaderMenu(String id) {
		super(id,new ODocumentModel());
		add(configure = new ConfigureDashboardCommand("configure", getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick(Optional<AjaxRequestTarget> targetOptional) {
				super.onClick(targetOptional);
				onEdit(targetOptional);
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(findParent(IDashboardContainer.class).getCurrentDashboard() != null);
			}
		});

		add(close = new KeepUnsavedDashboardCommand("close", getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick(Optional<AjaxRequestTarget> targetOptional) {
				super.onClick(targetOptional);
				onView(targetOptional);
			}});
		
		close.setVisibilityAllowed(false);
		
		add(commands = new RepeatingView("commands"));
		commands.setVisible(true);
		commands.setVisibilityAllowed(false);
		//methods.overrideBootstrapType(null);
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		getParent().setOutputMarkupId(true);
	}
	
	@Override
	protected void onBeforeRender() {
		IDashboardContainer daschboardContainer = findParent(IDashboardContainer.class);
		if (daschboardContainer.getCurrentDashboard()!=null){
			DisplayMode dashboardModeModel = daschboardContainer.getCurrentDashboard().getSelfComponent().getModeObject();
			if(DisplayMode.EDIT.equals(dashboardModeModel)) {
				onEdit();
			} else {
				onView();
			}
		}	
		super.onBeforeRender();
	}
	@Override
	protected void onInitialize() {
		super.onInitialize();
		OMethodsManager.get().populate(this, MethodPlace.DASHBOARD_SETTINGS, getModel());
	}
	
	private void onEdit(Optional<AjaxRequestTarget> targetOptional){
		onEdit();
		targetOptional.ifPresent(target -> target.add(getParent()));
	}

	private void onEdit(){
		configure.setVisibilityAllowed(false);
		close.setVisibilityAllowed(true);
		commands.setVisibilityAllowed(true);
	}
	
	private void onView(Optional<AjaxRequestTarget> targetOptional){
		onView();
		targetOptional.ifPresent(target -> target.add(getParent()));
	}
	private void onView(){
		configure.setVisibilityAllowed(true);
		close.setVisibilityAllowed(false);
		commands.setVisibilityAllowed(false);
	}
	
	@Override
	public void onEvent(IEvent<?> event) {
		if(event.getPayload() instanceof SwitchDashboardTabEvent) {
			((SwitchDashboardTabEvent)event.getPayload()).getTarget().add(getParent());
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
