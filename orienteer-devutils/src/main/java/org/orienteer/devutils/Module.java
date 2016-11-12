package org.orienteer.devutils;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInitializationListener;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.devutils.inspector.LiveSessionsPage;
import org.apache.wicket.markup.ComponentTag;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.core.web.BasePage;
import org.orienteer.devutils.web.ToolsPage;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketconsole.devutils.WicketConsolePage;

import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-devutils' module
 */
public class Module extends AbstractOrienteerModule implements IComponentInitializationListener{

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
		app.mountPages("org.orienteer.devutils.web");
		WicketConsolePage.setWicketConsolePageImplementation(ToolsPage.class);
		app.mountPackage("/devutils", LiveSessionsPage.class);
		app.mountPage("/wicket-console", WicketConsolePage.class);
		app.registerWidgets("org.orienteer.devutils.component.widget");
		app.getComponentInitializationListeners().add(this);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.getComponentInitializationListeners().remove(this);
		WicketConsolePage.setWicketConsolePageImplementation(null);
		app.unmountPages("org.orienteer.devutils.web");
		app.unmount("/devutils/"+LiveSessionsPage.class.getSimpleName());
		app.unmount("/wicket-console");
		app.unregisterWidgets("org.orienteer.devutils.component.widget");
	}

	@Override
	public void onInitialize(Component component) {
		if(component instanceof BasePage) {
			BasePage<?> page = (BasePage<?>)component;
			page.addUiPlugin(new DebugBar(page.nextUiPluginComponentId())
									.add(new AttributeAppender("style", "position: fixed; left: 0; bottom: 0; top: inherit;", "; ")));
		}
	}
	
}
