package org.orienteer.etl.component;

import com.google.common.base.Throwables;
import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLProcessor;

import org.apache.wicket.util.lang.Exceptions;
import org.apache.wicket.util.string.Strings;
import org.joor.Reflect;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.component.visualizer.UIVisualizersRegistry;
import org.orienteer.core.dao.DAOField;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.web.AbstractWidgetDisplayModeAwarePage;
import org.orienteer.core.web.ODocumentPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
/**
 * 
 * OrientDB ETL config object
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOETLConfig.CLASS_NAME, orderOffset = 50)
public interface IOETLConfig extends IOTask<IOTaskSessionPersisted> {
	
	public static final Logger LOG = LoggerFactory.getLogger(IOTask.class);
	public static final String CLASS_NAME = "OETLConfig";

	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_JAVASCRIPT)
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
