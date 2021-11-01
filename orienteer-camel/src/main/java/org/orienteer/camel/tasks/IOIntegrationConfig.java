package org.orienteer.camel.tasks;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.dao.OrienteerOClass;
import org.orienteer.core.dao.OrienteerOProperty;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.transponder.annotation.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.id.ORID;

/**
 * Wrapper for OIntegrationConfig ODocuments
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(IOIntegrationConfig.CLASS_NAME)
@OrienteerOClass(orderOffset = 50)
public interface IOIntegrationConfig extends IOTask<IOTaskSessionPersisted> {
    public static final Logger LOG = LoggerFactory.getLogger(IOIntegrationConfig.class);
	public static final String CLASS_NAME = "OIntegrationConfig";
	
	public static final MetaDataKey<Map<ORID,OCamelContext>> INTEGRATION_SESSIONS_KEY = new MetaDataKey<Map<ORID,OCamelContext>>()
	{
		private static final long serialVersionUID = 1L;
	};
	
	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_CODE)
	public String getScript();
	public void setScript(String value);
	
	public default OCamelContext getOrMakeContext(){
		OCamelContext context;
		Map<ORID,OCamelContext> contextMap = Application.get().getMetaData(INTEGRATION_SESSIONS_KEY);
		if (contextMap.containsKey(getDocument().getIdentity())){
			context = contextMap.get(getDocument().getIdentity());
		}else{
			context = new OCamelContext(this);
			contextMap.put(getDocument().getIdentity(), context);
		}
		return context;
	}
	
	public static void clearContext(CamelContext context) throws Exception{
		List<RouteDefinition> definitions = context.getRouteDefinitions();
		if (!definitions.isEmpty()){
			context.removeRouteDefinitions(new ArrayList<RouteDefinition>(definitions));
		}
	}

	@Override
	public default OTaskSessionRuntime<IOTaskSessionPersisted> startNewSession() {
		final OCamelContext context = getOrMakeContext();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (context.getStatus().isSuspended()){
						context.resume();
						//target.add(CamelWidget.this.form);
					}else if (!context.getStatus().isStarted()){
						clearContext(context);
						String script = getScript();
						RoutesDefinition routes = context.loadRoutesDefinition(new ByteArrayInputStream( script.getBytes()));
						context.addRouteDefinitions(routes.getRoutes());
						context.start();
					}
				} catch (Exception e) {
					LOG.error("Cannot start or resume Camel Context",e);
				}
			}
		}).start();
		return context.getRuntimeSession();
	}


}
