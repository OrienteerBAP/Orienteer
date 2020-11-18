package org.orienteer.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.storage.impl.local.OAbstractPaginatedStorage;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

/**
 */
public class OMetricsOrientDB extends Collector {
	
	private static final Logger LOG = LoggerFactory.getLogger(OMetricsOrientDB.class);
	
	@Override
	public List<MetricFamilySamples> collect() {
		return new DBClosure<List<MetricFamilySamples>>() {
			@Override
			protected List<MetricFamilySamples> execute(ODatabaseSession db) {
				List<MetricFamilySamples> mfs = new ArrayList<MetricFamilySamples>();
				//TODO: Check how to support in OrientDB 3
//				mfs.add(new GaugeMetricFamily("orientdb_frozen", "Is DB frozen and in RO mode", db.isFrozen()?1.0:0.0));
				
				GaugeMetricFamily count = new GaugeMetricFamily("orientdb_count", "Count of instances per class including subclasses", Collections.singletonList("class"));
				long total = 0;
				Collection<OClass> classes = ((ODatabaseDocumentInternal)db).getMetadata().getImmutableSchemaSnapshot().getClasses();
				for (OClass oClass : classes) {
					count.addMetric(Collections.singletonList(oClass.getName()), oClass.count());
					total+=oClass.count(false);
				}
				mfs.add(count);
				mfs.add(new GaugeMetricFamily("orientdb_count_total", "Count of total instances", total));
				return mfs;
			}
		}.execute();
	}
	
}
