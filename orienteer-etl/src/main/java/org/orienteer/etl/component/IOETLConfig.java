package org.orienteer.etl.component;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.etl.OETLProcessor;

import org.apache.wicket.util.string.Strings;
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
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.web.AbstractWidgetDisplayModeAwarePage;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.etl.tasks.OETLTaskSession;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
/**
 * 
 * OrientDB ETL config object
 *
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOETLConfig.CLASS_NAME)
public interface IOETLConfig extends IOTask {
	
	public static final String CLASS_NAME = "OETLConfig";

	@DAOField(visualization = UIVisualizersRegistry.VISUALIZER_CODE)
	public String getConfig();
	public void setConfig(String value);
	
	@OMethod(
		order=10,bootstrap=BootstrapType.SUCCESS,icon = FAIconType.play,
		permission="EXECUTE",
		filters={
				@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
				@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),
//					@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE|DATA_TABLE"),
		}
	)
	public default void run(IMethodContext data){
		OTaskSessionRuntime newSession = startNewSession();
		AbstractWidgetDisplayModeAwarePage<ODocument> page = new ODocumentPage(new ODocumentModel(newSession.getOTaskSessionPersisted().getDocument())).setModeObject(DisplayMode.VIEW);
		
		data.getCurrentWidget().setResponsePage(page);
	}

	@Override
	public default OTaskSessionRuntime startNewSession() {
		
		final OETLTaskSession session = new OETLTaskSession();
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
						session.appendOut("ETL Processor runtime error!");
						printCause(e, session);
					}finally{
						session.finish();
					}
				}
			});

			session.setDeleteOnFinish(isAutodeleteSessions());
			session.setOTask(this);

			thread.start();
		} catch (Exception e) {
			session.appendOut("ETL Processor execute error!");
			printCause(e, session);
			session.finish();
		}
		
		
		return session;
	}

	static void printCause(Throwable e,OETLTaskSession session){
		if (!Strings.isEmpty(e.getMessage())){
			session.appendOut(e.getMessage());
		}
		if (e.getCause()!=null){
			printCause(e.getCause(), session);
		}
	}

}
