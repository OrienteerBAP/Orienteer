package org.orienteer.camel.tasks;

import java.util.Map;

import org.apache.camel.impl.DefaultCamelContext;
import org.orienteer.camel.component.OrientDBComponent;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.OTaskSessionRuntime;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Extension of {@link DefaultCamelContext} to store {@link OTaskSessionRuntime}
 */
public class OCamelContext extends DefaultCamelContext {
	private OTaskSessionRuntime<IOTaskSessionPersisted> runtime;
	
	public OCamelContext(IOIntegrationConfig config) {
		IOrientDbSettings dbSettings = OrientDbWebApplication.get().getOrientDbSettings();
		OrientDbWebSession session = OrientDbWebSession.get();
		Map<String, String> properties = getGlobalOptions();
		properties.put(OrientDBComponent.DB_URL, dbSettings.getDbType() + ":" + dbSettings.getDbName());
		properties.put(OrientDBComponent.DB_USERNAME, session.getUsername());
		properties.put(OrientDBComponent.DB_PASSWORD, session.getPassword());
		setGlobalOptions(properties);
		runtime = OTaskSessionRuntime.simpleSession();
		getManagementStrategy().addEventNotifier(new CamelEventHandler(this));
		runtime.getOTaskSessionPersisted()
				.setTask(config)
				.setCallback(new OCamelTaskSessionCallback(this))
				.setDeleteOnFinish(config.isAutodeleteSessions())
				.setFinalProgress(getRoutes().size())
				.getOTaskSessionPersisted().persist();
	}
	
	public OTaskSessionRuntime<IOTaskSessionPersisted> getRuntimeSession() {
		return runtime;
	}
	
	public IOTaskSessionPersisted getPersistedSession() {
		return runtime.getOTaskSessionPersisted();
	}
	
	public OCamelContext persist() {
		getPersistedSession().persist();
		return this;
	}
}
