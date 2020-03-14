package org.orienteer.metrics;


import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.orienteer.core.OrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

/**
 * {@link IRequestCycleListener} to monitor requests
 */
public class OMetricsRequestCycleListener implements IRequestCycleListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(OMetricsRequestCycleListener.class);
	
	private static OMetricsRequestCycleListener listener;
	
	
	private static final MetaDataKey<Histogram.Timer> REQUESTS_HISTOGRAM_KEY = new MetaDataKey<Histogram.Timer>() {};
	
	private static final Counter COUNTER_EXCEPTIONS = Counter.build()
															.namespace("wicket")
		     												.name("exceptions_count")
		     												.help("Total number of exceptions")
		     												.labelNames("ajax")
		     												.create();
	
	private static final Counter COUNTER_EXECUTIONS = Counter.build()
																.namespace("wicket")
																	.name("executions")
																	.help("Total number request handlers executions")
																	.labelNames("ajax", "handler")
																	.create();
	
	private static final Histogram HISTOGRAM_REQUESTS = Histogram.build()
															.namespace("wicket")
															.name("requests")
															.help("Request times and counts histogram")
															.labelNames("ajax")
															.create();
	
	private OMetricsRequestCycleListener() {
		CollectorRegistry.defaultRegistry.register(COUNTER_EXCEPTIONS);
		//Just to init by 0 sub counters
		COUNTER_EXCEPTIONS.labels(Boolean.TRUE.toString()).inc(0);
		COUNTER_EXCEPTIONS.labels(Boolean.FALSE.toString()).inc(0);
		CollectorRegistry.defaultRegistry.register(COUNTER_EXECUTIONS);
		CollectorRegistry.defaultRegistry.register(HISTOGRAM_REQUESTS);
	}
	
	@Override
	public void onBeginRequest(RequestCycle cycle) {
		Histogram.Timer requestTimer = HISTOGRAM_REQUESTS
										.labels(Boolean.toString(((WebRequest)cycle.getRequest()).isAjax()))
										.startTimer();
		cycle.setMetaData(REQUESTS_HISTOGRAM_KEY, requestTimer);
	}
	
	@Override
	public void onEndRequest(RequestCycle cycle) {
		Histogram.Timer requestTimer = cycle.getMetaData(REQUESTS_HISTOGRAM_KEY);
		requestTimer.observeDuration();
	}

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		COUNTER_EXCEPTIONS.labels(Boolean.toString(((WebRequest)cycle.getRequest()).isAjax())).inc();
		return null;
	}
	
	@Override
	public void onRequestHandlerExecuted(RequestCycle cycle, IRequestHandler handler) {
		COUNTER_EXECUTIONS.labels(Boolean.toString(((WebRequest)cycle.getRequest()).isAjax()),
									handler.getClass().getSimpleName()).inc();
	}
	
	protected void onDestroy() {
		CollectorRegistry.defaultRegistry.unregister(COUNTER_EXECUTIONS);
		CollectorRegistry.defaultRegistry.unregister(COUNTER_EXCEPTIONS);
		CollectorRegistry.defaultRegistry.unregister(HISTOGRAM_REQUESTS);
	}
	
	
	public static synchronized void install(OrienteerWebApplication app) {
		if(listener!=null) deinstall(app);
		listener = new OMetricsRequestCycleListener();
		app.getRequestCycleListeners().add(listener);
	}

	public static synchronized void deinstall(OrienteerWebApplication app) {
		if(listener!=null) {
			listener.onDestroy();
			app.getRequestCycleListeners().remove(listener);
			listener = null;
		}
	}
	
}
