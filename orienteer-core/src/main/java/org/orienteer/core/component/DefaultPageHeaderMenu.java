package org.orienteer.core.component;

import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.event.SwitchDashboardTabEvent;
import org.orienteer.core.method.MethodPlace;
import org.orienteer.core.method.MethodsView;
import org.orienteer.core.widget.IDashboardContainer;
import org.orienteer.core.widget.command.ConfigureDashboardCommand;
import org.orienteer.core.widget.command.KeepUnsavedDashboardCommand;

import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * 
 * Default breadcrumb menu
 *
 */
public class DefaultPageHeaderMenu extends GenericPanel<ODocument> {
	private static final long serialVersionUID = 1L;
	private Component configure;
	private Component close;
	private RepeatingView commands;
	private MethodsView methods;

	public DefaultPageHeaderMenu(String id) {
		super(id,new ODocumentModel());
		add(configure = new ConfigureDashboardCommand("configure", getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick(Optional<AjaxRequestTarget> targetOptional) {
				super.onClick(targetOptional);
				onEdit(targetOptional);
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
		methods = new MethodsView(commands, getModel(),MethodPlace.DASHBOARD_SETTINGS,null);
		//methods.overrideBootstrapType(null);
	}
	
	@Override
	protected void onConfigure() {
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
		methods.loadMethods();
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
}
