package org.orienteer.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.storage.impl.local.OAbstractPaginatedStorage;
import com.orientechnologies.orient.core.storage.impl.local.statistic.OPerformanceStatisticManager;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 */
public class OMetricsOrientDB extends Collector {
	
	private static final Logger LOG = LoggerFactory.getLogger(OMetricsOrientDB.class);
	
	/**
	 *  Utility interface to pack building of {@link MetricFamilySamples} collection
	 *  TODO: Uncomment when performance metrics gathering will be fixed in OrientDB
	 *  https://github.com/orientechnologies/orientdb/issues/9169
	 */
	/*@FunctionalInterface
	private static interface IAddMetricFunction {
		void addMetric(String name, boolean counter, Function<OPerformanceStatisticManager, Number> getter);
	}*/
	
	@Override
	public List<MetricFamilySamples> collect() {
		return new DBClosure<List<MetricFamilySamples>>() {
			@Override
			protected List<MetricFamilySamples> execute(ODatabaseDocument db) {
				List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
				//TODO: Check how to support in OrientDB 3
//				mfs.add(new GaugeMetricFamily("orientdb_frozen", "Is DB frozen and in RO mode", db.isFrozen()?1.0:0.0));
				
				GaugeMetricFamily count = new GaugeMetricFamily("orientdb_count", "Count of instances per class including subclasses", Collections.singletonList("class"));
				long total = 0;
				Collection<OClass> classes = ((ODatabaseDocumentTx)db).getMetadata().getImmutableSchemaSnapshot().getClasses();
				for (OClass oClass : classes) {
					count.addMetric(Collections.singletonList(oClass.getName()), oClass.count());
					total+=oClass.count(false);
				}
				mfs.add(count);
				mfs.add(new GaugeMetricFamily("orientdb_count_total", "Count of total instances", total));
				
				
				/*OPerformanceStatisticManager perf = getPerformanceStatisticManager(db);
				if(perf!=null) {
					IAddMetricFunction doOp = (name, counter, getter) -> {
						Number value = getter.apply(perf);
						String underScoreName = "orientdb_"+name;
						String help = "Metric for '"+name+"'";
						mfs.add(counter ? new CounterMetricFamily(underScoreName, help, value.doubleValue())
										: new GaugeMetricFamily(underScoreName, help, value.doubleValue()));
					};
					doOp.addMetric("cache_hits", false, OPerformanceStatisticManager::getCacheHits);
					doOp.addMetric("commit_time", false, OPerformanceStatisticManager::getCommitTime);
					doOp.addMetric("exclusive_write_cache_size", false, OPerformanceStatisticManager::getExclusiveWriteCacheSize);
					doOp.addMetric("full_checkpoint_count", true, OPerformanceStatisticManager::getFullCheckpointCount);
					doOp.addMetric("full_checkpoint_time", true, OPerformanceStatisticManager::getFullCheckpointTime);
					doOp.addMetric("read_cache_size", false, OPerformanceStatisticManager::getReadCacheSize);
					doOp.addMetric("read_speed_from_cache_in_pages", false, OPerformanceStatisticManager::getReadSpeedFromCacheInPages);
					doOp.addMetric("read_speed_from_files_in_pages", false, OPerformanceStatisticManager::getReadSpeedFromFileInPages);
					doOp.addMetric("wal_cache_overflow_count", true, OPerformanceStatisticManager::getWALCacheOverflowCount);
					doOp.addMetric("wal_flush_time", false, OPerformanceStatisticManager::getWALFlushTime);
					doOp.addMetric("wal_log_record_time", false, OPerformanceStatisticManager::getWALLogRecordTime);
					doOp.addMetric("wal_size", false, OPerformanceStatisticManager::getWALSize);
					doOp.addMetric("wal_start_ao_log_record_time", false, OPerformanceStatisticManager::getWALStartAOLogRecordTime);
					doOp.addMetric("wal_stop_ao_log_record_time", false, OPerformanceStatisticManager::getWALStopAOLogRecordTime);
					doOp.addMetric("write_cache_flush_operations_time", false, OPerformanceStatisticManager::getWriteCacheFlushOperationsTime);
					doOp.addMetric("write_cache_overflow_count", true, OPerformanceStatisticManager::getWriteCacheOverflowCount);
					doOp.addMetric("write_cache_pages_per_flush", false, OPerformanceStatisticManager::getWriteCachePagesPerFlush);
					doOp.addMetric("write_cache_size", false, OPerformanceStatisticManager::getWriteCacheSize);
					doOp.addMetric("write_speed_in_cache_in_pages", false, OPerformanceStatisticManager::getWriteSpeedInCacheInPages);
				}*/
				return mfs;
			}
		}.execute();
	}
	
	/*public static OPerformanceStatisticManager getPerformanceStatisticManager() {
		return new DBClosure<OPerformanceStatisticManager>() {

			@Override
			protected OPerformanceStatisticManager execute(ODatabaseDocument db) {
				return getPerformanceStatisticManager(db);
			}
		}.execute();
	}
	
	public static OPerformanceStatisticManager getPerformanceStatisticManager(ODatabaseDocument db) {
		if(db instanceof ODatabaseDocumentTx) {
			OStorage storage = ((ODatabaseDocumentTx)db).getStorage();
			if(storage instanceof OAbstractPaginatedStorage) {
				return ((OAbstractPaginatedStorage)storage).getPerformanceStatisticManager();
			}
		}
		return null;
	}*/

}
