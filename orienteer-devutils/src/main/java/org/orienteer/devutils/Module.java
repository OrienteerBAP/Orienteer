package org.orienteer.devutils;

import org.apache.wicket.Component;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.application.IComponentInitializationListener;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.devutils.debugbar.DebugBarInitializer;
import org.apache.wicket.devutils.debugbar.IDebugBarContributor;
import org.apache.wicket.devutils.debugbar.InspectorDebugPanel;
import org.apache.wicket.devutils.debugbar.PageSizeDebugPanel;
import org.apache.wicket.devutils.debugbar.SessionSizeDebugPanel;
import org.apache.wicket.devutils.debugbar.VersionDebugContributor;
import org.apache.wicket.devutils.inspector.LiveSessionsPage;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.web.BasePage;
import org.orienteer.devutils.web.ToolsPage;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketconsole.devutils.WicketConsoleDebugPanel;
import ru.ydn.wicket.wicketconsole.devutils.WicketConsolePage;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import static ru.ydn.wicket.wicketorientdb.security.OSecurityHelper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link IOrienteerModule} for 'orienteer-devutils' module
 */
public class Module extends AbstractOrienteerModule implements IComponentInitializationListener{
	
	private static final List<IDebugBarContributor> CONTRIBUTORS = Arrays.asList(
			WicketConsoleDebugPanel.DEBUG_BAR_CONTRIB,
			VersionDebugContributor.DEBUG_BAR_CONTRIB,
			InspectorDebugPanel.DEBUG_BAR_CONTRIB,
			SessionSizeDebugPanel.DEBUG_BAR_CONTRIB,
			PageSizeDebugPanel.DEBUG_BAR_CONTRIB);

	protected Module() {
		super("devutils", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		//Install data model
		ODocument moduleDoc = new ODocument(OMODULE_CLASS);
		moduleDoc.field(OMODULE_ACTIVATE, false);
		return moduleDoc;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		app.mountPackage("org.orienteer.devutils.web");
		WicketConsolePage.setWicketConsolePageImplementation(ToolsPage.class);
		app.mountPackage("/devutils", LiveSessionsPage.class);
		app.mountPage("/wicket-console", WicketConsolePage.class);
		app.registerWidgets("org.orienteer.devutils.component.widget");
		app.getComponentInitializationListeners().add(this);
		if(!app.getDebugSettings().isDevelopmentUtilitiesEnabled())
		{
			app.getDebugSettings().setDevelopmentUtilitiesEnabled(true);
			List<IDebugBarContributor> contributors = new ArrayList<IDebugBarContributor>(DebugBar.getContributors(app));
			CONTRIBUTORS.forEach((c) -> {if(!contributors.contains(c)) contributors.add(c);});
			DebugBar.setContributors(contributors, app);
		}
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.getDebugSettings()
				.setDevelopmentUtilitiesEnabled(app.getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT);
		app.getComponentInitializationListeners().remove(this);
		WicketConsolePage.setWicketConsolePageImplementation(null);
		app.unmountPackage("org.orienteer.devutils.web");
		app.unmount("/devutils/"+LiveSessionsPage.class.getSimpleName());
		app.unmount("/wicket-console");
		app.unregisterWidgets("org.orienteer.devutils.component.widget");
	}

	@Override
	public void onInitialize(Component component) {
		if(component instanceof BasePage && isAllowed(FEATURE_RESOURCE, "devutils", OrientPermission.CREATE,
																					OrientPermission.READ,
																					OrientPermission.UPDATE,
																					OrientPermission.DELETE)) {
			BasePage<?> page = (BasePage<?>)component;
			DebugBar debugBar = new DebugBar(page.nextUiPluginComponentId());
			debugBar.add(new AttributeAppender("style", "position: fixed; "
													  + "left: 0; "
													  + "bottom: 0; "
													  + "top: inherit; "
													  + "z-index: 99999", 
													  "; "));
			page.addUiPlugin(debugBar);
		}
	}
	
}
