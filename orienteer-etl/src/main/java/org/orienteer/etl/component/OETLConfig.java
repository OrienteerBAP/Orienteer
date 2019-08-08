package org.orienteer.etl.component;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.util.string.Strings;
import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.IMethodContext;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.OMethod;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.OTask;
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
public class OETLConfig extends OTask {
	private static final long serialVersionUID = 1L;
	private static final String ETL_CONFIG_FIELD="config";

	public OETLConfig(ODocument iDocument) {
		super(iDocument);
	}
	
	@OMethod(
		order=10,bootstrap=BootstrapType.SUCCESS,icon = FAIconType.play,
		permission="EXECUTE",
		filters={
				@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
				@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),
//					@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE|DATA_TABLE"),
		}
	)
	public void run(IMethodContext data){
		OTaskSessionRuntime newSession = startNewSession();
		AbstractWidgetDisplayModeAwarePage<ODocument> page = new ODocumentPage(new ODocumentModel(newSession.getOTaskSessionPersisted().getDocument())).setModeObject(DisplayMode.VIEW);
		
		data.getCurrentWidget().setResponsePage(page);
	}

	@Override
	public OTaskSessionRuntime startNewSession() {
		
		/*final OETLTaskSession session = new OETLTaskSession();
		session.start();

		try {
			final String configuration = getDocument().field(ETL_CONFIG_FIELD);
			final OrienteerETLProcessor processor = OrienteerETLProcessor.parseConfigRecord(session,configuration);

			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						processor.doExecute();
					} catch (Exception e) {
						session.appendOut("ETL Processor runtime error!");
						printCause(e, session);
					}finally{
						session.finish();
					}
				}
			});

			session.setCallback(new OETLTaskSessionCallback(thread,processor));
			session.setDeleteOnFinish((Boolean) document.field(OTask.Field.AUTODELETE_SESSIONS.fieldName()));
			session.setOTask(this);

			thread.start();
		} catch (Exception e) {
			session.appendOut("ETL Processor execute error!");
			printCause(e, session);
			session.finish();
		}
		
		
		return session;

		 */
		throw new UnsupportedOperationException();
	}

	private void printCause(Throwable e,OETLTaskSession session){
		if (!Strings.isEmpty(e.getMessage())){
			session.appendOut(e.getMessage());
		}
		if (e.getCause()!=null){
			printCause(e.getCause(), session);
		}
	}

}
