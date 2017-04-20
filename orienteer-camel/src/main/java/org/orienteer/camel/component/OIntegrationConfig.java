package org.orienteer.camel.component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.orienteer.camel.behavior.OIntegrationConfigStopBehavior;
import org.orienteer.camel.tasks.CamelEventHandler;
import org.orienteer.camel.tasks.OCamelTaskSessionCallback;
import org.orienteer.camel.widget.CamelWidget;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Wrapper for OIntegrationConfig ODocuments
 *
 */

public class OIntegrationConfig extends ODocumentWrapper {
	private static final long serialVersionUID = 1L;

/////////////////////////////////////////////////////////////////////////////////////////////////////	
	@ClassOMethod(
			order=10,bootstrap=BootstrapType.SUCCESS,icon = FAIconType.play,
			filters={
					@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
//					@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE|DATA_TABLE"),
			}
	)
	public void start(IMethodEnvironmentData data){
		final CamelContext context = getOrMakeContextByRid(getDocument().getIdentity().toString(),data.getCurrentWidget());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (context.getStatus().isSuspended()){
						context.resume();
						//target.add(CamelWidget.this.form);
					}else if (!context.getStatus().isStarted()){
						clearContext(context);
						String script = getDocument().field("script");
						RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
						context.addRouteDefinitions(routes.getRoutes());
						context.start();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	@ClassOMethod(
			order = 30,bootstrap=BootstrapType.DANGER,icon = FAIconType.stop,
			filters={@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),},
			behaviors={OIntegrationConfigStopBehavior.class}
	)
	public void stop(IMethodEnvironmentData data){
		try {

			CamelContext context = getOrMakeContextByRid(getDocument().getIdentity().toString(),data.getCurrentWidget());
			try {
				context.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	} 
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	@ClassOMethod(
			order = 20,bootstrap=BootstrapType.WARNING,icon = FAIconType.pause,
			filters={
					@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
					},
			behaviors={OIntegrationConfigStopBehavior.class}
	)
	public void suspend(IMethodEnvironmentData data){
		try {
			final CamelContext context = getOrMakeContext(data.getCurrentWidget());
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ServiceStatus status = context.getStatus();
						if (status.isSuspended()){
							context.start();
						}else if(status.isStarted()){
							context.suspend();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	public OIntegrationConfig(ODocument doc) {
		super(doc);
	}

	public CamelContext getOrMakeContextByRid(String rid,Component component){
		CamelContext context;
		Map<String,CamelContext> contextMap = Application.get().getMetaData(CamelWidget.INTEGRATION_SESSIONS_KEY);
		if (contextMap.containsKey(rid)){
			context = contextMap.get(rid);
		}else{
			IOrientDbSettings dbSettings = OrientDbWebApplication.get().getOrientDbSettings();
			OrientDbWebSession session = OrientDbWebSession.get();
			if (session.getUsername()==null){
				throw new UnauthorizedActionException(component, Component.RENDER);
			}
			context = new DefaultCamelContext();
			Map<String, String> properties = context.getProperties();
			properties.put(OrientDBComponent.DB_URL, dbSettings.getDBUrl());
			properties.put(OrientDBComponent.DB_USERNAME, session.getUsername());
			properties.put(OrientDBComponent.DB_PASSWORD, session.getPassword());
			context.setProperties(properties);
			
			context.getManagementStrategy().addEventNotifier(new CamelEventHandler(new OCamelTaskSessionCallback(context),rid,context));

			contextMap.put(rid, context);
		}
		return context;
	}
	
	public void clearContext(CamelContext context) throws Exception{
		List<RouteDefinition> definitions = context.getRouteDefinitions();
		if (!definitions.isEmpty()){
			context.removeRouteDefinitions(new ArrayList<RouteDefinition>(definitions));
		}
	}
	
	public CamelContext getOrMakeContext(Component component){
		return getOrMakeContextByRid(getDocument().getIdentity().toString(),component);
	}


}
