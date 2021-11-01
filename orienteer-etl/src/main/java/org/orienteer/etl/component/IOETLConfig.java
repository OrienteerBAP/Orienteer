package org.orienteer.etl.component;

import org.joor.Reflect;
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

import com.google.common.base.Throwables;
import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.etl.OETLProcessor;
/**
 * 
 * OrientDB ETL config object
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = IOETLConfig.CLASS_NAME)
@OrienteerOClass(orderOffset = 50)
public interface IOETLConfig extends IOTask<IOTaskSessionPersisted> {
	
	public static final Logger LOG = LoggerFactory.getLogger(IOTask.class);
	public static final String CLASS_NAME = "OETLConfig";

	@OrienteerOProperty(visualization = UIVisualizersRegistry.VISUALIZER_JAVASCRIPT)
	public String getConfig();
	public void setConfig(String value);
	
	@Override
	public default OTaskSessionRuntime<IOTaskSessionPersisted> startNewSession() {
		
		final OTaskSessionRuntime<IOTaskSessionPersisted> session = OTaskSessionRuntime.simpleSession(this);
		session.start();

		try {
			final String configuration = getConfig();
			final OETLProcessor processor = new OrienteerETLProcessorConfigurator()
													.parseConfigRecord(session,configuration);
//			final OrienteerETLProcessorConfigurator processor = OrienteerETLProcessorConfigurator.parseConfigRecord(session,configuration);

			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						processor.execute();
					} catch (Exception e) {
						LOG.error("ETL Processor runtime error!", e);
						session.getOTaskSessionPersisted()
							.appendError("ETL Processor runtime error!\n"+Throwables.getStackTraceAsString(e))
							.persist();
						Reflect.on(processor).call("end");
					}finally{
						session.finish();
					}
				}
			});

			thread.start();
		} catch (Exception e) {
			LOG.error("ETL Processor execute error!", e);
			session.getOTaskSessionPersisted()
				.appendError("ETL Processor execute error!\n"+Throwables.getStackTraceAsString(e))
				.persist();
			session.finish();
		}
		
		
		return session;
	}

}
