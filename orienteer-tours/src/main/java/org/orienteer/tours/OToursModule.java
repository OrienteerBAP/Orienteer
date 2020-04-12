package org.orienteer.tours;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.application.IComponentInitializationListener;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.component.DefaultPageHeaderMenu;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.tours.model.IOTourItem;
import org.orienteer.tours.model.IOTour;
import org.orienteer.tours.model.IOTourStep;
import org.orienteer.tours.rest.OToursRestResources;
import org.orienteer.wicketjersey.WicketJersey;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'tours' module
 */
public class OToursModule extends AbstractOrienteerModule{
	
	private static class CustomizationBehavior extends Behavior {
		
		@Override
		public void renderHead(Component component, IHeaderResponse response) {
			if(component instanceof Page) {
				OrienteerWebApplication.get()
				.getServiceInstance(ITourPlugin.class)
				.renderHeader((Page)component, response);
				response.render(JavaScriptHeaderItem.forReference(TOURS_JS, "tours"));
			}
		}
	}
	private static class CustomizationListener implements IComponentInstantiationListener {
		
		@Override
		public void onInstantiation(Component component) {
			if(component instanceof Page) {
				component.add(new CustomizationBehavior());
			}
			
		}
	}
	
	private static final JavaScriptResourceReference TOURS_JS = new JavaScriptResourceReference(OToursModule.class, "tours.js");

	
	
	private IComponentInstantiationListener customizationListener = new CustomizationListener();
	
	protected OToursModule() {
		super("tours", 1);
	}
	
	
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.describeAndInstallSchema(IOTour.class, IOTourStep.class);
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		app.mountPackage("org.orienteer.tours.web");
		WicketJersey.mount("/otours", OToursRestResources.class.getPackage().getName());
		app.getComponentInstantiationListeners().add(customizationListener);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		app.getComponentInstantiationListeners().remove(customizationListener);
		app.unmount("/otours");
		app.unmountPackage("org.orienteer.tours.web");
		super.onDestroy(app, db);
	}

}
