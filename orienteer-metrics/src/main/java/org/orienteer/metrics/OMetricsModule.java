package org.orienteer.metrics;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.impl.local.statistic.OPerformanceStatisticManager;

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
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		DefaultExports.initialize();
		OMetricsRequestCycleListener.install(app);
		OMetricSessionListener.install(app);
		new OMetricsOrientDB().register();
//		OPerformanceStatisticManager psm = OMetricsOrientDB.getPerformanceStatisticManager();
		//WorkAround to avoid NPEs in logs
		//TODO: Remove when https://github.com/orientechnologies/orientdb/issues/9169 will be fixed
//		psm.startThreadMonitoring();
//		psm.getSessionPerformanceStatistic().startWALFlushTimer();
//		psm.getSessionPerformanceStatistic().stopWALFlushTimer();
//		psm.stopThreadMonitoring();
		//End of block to delete after issue fixing
		
//		psm.startMonitoring();
		app.mountPackage(OMetricsModule.class.getPackage().getName());
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		app.unmountPackage(OMetricsModule.class.getPackage().getName());
//		OMetricsOrientDB.getPerformanceStatisticManager().stopMonitoring();
		OMetricSessionListener.deinstall(app);
		OMetricsRequestCycleListener.deinstall(app);
		CollectorRegistry.defaultRegistry.clear();
		super.onDestroy(app, db);
	}

}
