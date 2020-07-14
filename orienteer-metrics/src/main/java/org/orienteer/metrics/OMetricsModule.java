package org.orienteer.metrics;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.impl.ODocument;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * {@link IOrienteerModule} for 'metrics' module
 */
//TODO: Enable when https://github.com/orientechnologies/orientdb/issues/9169 will be done
public class OMetricsModule extends AbstractOrienteerModule{
	
	
	protected OMetricsModule() {
		super("metrics", 1);
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInitialize(app, db);
		DefaultExports.initialize();
		OMetricsRequestCycleListener.install(app);
		OMetricSessionListener.install(app);
		new OMetricsOrientDB().register();
		app.mountPackage(OMetricsModule.class.getPackage().getName());
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		app.unmountPackage(OMetricsModule.class.getPackage().getName());
		OMetricSessionListener.deinstall(app);
		OMetricsRequestCycleListener.deinstall(app);
		CollectorRegistry.defaultRegistry.clear();
		super.onDestroy(app, db);
	}

}
