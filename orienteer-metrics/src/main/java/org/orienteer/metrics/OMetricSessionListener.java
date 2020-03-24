package org.orienteer.metrics;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.session.ISessionStore;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

/**
 * {@link ISessionListener} to monitor creation and unbounding of sessions
 */
public class OMetricSessionListener implements ISessionListener, ISessionStore.BindListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(OMetricSessionListener.class);
	
	private static OMetricSessionListener listener;
	
	
	private static final Counter COUNTER_CREATED_SESSIONS = Counter.build()
														.namespace("wicket")
														.subsystem("session")
														.name("created")
														.help("Sessions created")
														.create();
	
	private static final Counter COUNTER_BOUND_SESSIONS = Counter.build()
														.namespace("wicket")
														.subsystem("session")
														.name("bound")
														.help("Sessions bound")
														.create();
	
	private static final Counter COUNTER_UNBOUND_SESSIONS = Counter.build()
															.namespace("wicket")
															.subsystem("session")
															.name("unbound")
															.help("Sessions unbound")
															.create();
	
	
	private OMetricSessionListener() {
		CollectorRegistry.defaultRegistry.register(COUNTER_CREATED_SESSIONS);
		CollectorRegistry.defaultRegistry.register(COUNTER_BOUND_SESSIONS);
		CollectorRegistry.defaultRegistry.register(COUNTER_UNBOUND_SESSIONS);
	}

	@Override
	public void onCreated(Session session) {
		COUNTER_CREATED_SESSIONS.inc();
	}
	
	@Override
	public void bindingSession(Request request, Session newSession) {
		COUNTER_BOUND_SESSIONS.inc();
	}

	@Override
	public void onUnbound(String sessionId) {
		COUNTER_UNBOUND_SESSIONS.inc();
	}
	
	protected void onDestroy() {
		CollectorRegistry.defaultRegistry.unregister(COUNTER_CREATED_SESSIONS);
		CollectorRegistry.defaultRegistry.unregister(COUNTER_BOUND_SESSIONS);
		CollectorRegistry.defaultRegistry.unregister(COUNTER_UNBOUND_SESSIONS);
	}
	
	public static synchronized void install(OrienteerWebApplication app) {
		if(listener!=null) deinstall(app);
		listener = new OMetricSessionListener();
		app.getSessionListeners().add(listener);
		app.getSessionStore().registerBindListener(listener);
	}

	public static synchronized void deinstall(OrienteerWebApplication app) {
		if(listener!=null) {
			listener.onDestroy();
			app.getSessionStore().unregisterBindListener(listener);
			app.getSessionListeners().remove(listener);
			listener = null;
		}
	}


}
