package org.orienteer.etl.component;

import org.orienteer.core.component.BootstrapType;
import org.orienteer.core.component.FAIconType;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.method.ClassOMethod;
import org.orienteer.core.method.IMethodEnvironmentData;
import org.orienteer.core.method.OFilter;
import org.orienteer.core.method.filters.PlaceFilter;
import org.orienteer.core.method.filters.WidgetTypeFilter;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.web.AbstractWidgetDisplayModeAwarePage;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.etl.tasks.OETLTaskSession;
import org.orienteer.etl.tasks.OETLTaskSessionCallback;

import com.orientechnologies.orient.core.record.impl.ODocument;

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
	
	@ClassOMethod(
		order=10,bootstrap=BootstrapType.SUCCESS,icon = FAIconType.play,
		filters={
				@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE"),
				@OFilter(fClass = WidgetTypeFilter.class, fData = "parameters"),
//					@OFilter(fClass = PlaceFilter.class, fData = "STRUCTURE_TABLE|DATA_TABLE"),
		}
	)
	public void run(IMethodEnvironmentData data){
		OTaskSessionRuntime newSession = startNewSession();
		AbstractWidgetDisplayModeAwarePage<ODocument> page = new ODocumentPage(new ODocumentModel(newSession.getOTaskSessionPersisted().getDocument())).setModeObject(DisplayMode.VIEW);
		
		data.getCurrentWidget().setResponsePage(page);
	}

	@Override
	public OTaskSessionRuntime startNewSession() {
		
		final OETLTaskSession session = new OETLTaskSession();
		session.start();

		final String configuration = getDocument().field(ETL_CONFIG_FIELD);
		final OrienteerETLProcessor processor = OrienteerETLProcessor.parseConfigRecord(session,configuration);

		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				processor.doExecute();
				session.finish();
			}
		});

		session.setCallback(new OETLTaskSessionCallback(thread,processor));
		session.setDeleteOnFinish((Boolean) document.field(OTask.Field.AUTODELETE_SESSIONS.fieldName()));
		session.setOTask(this);

		thread.start();
		
		return session;
	}

}
