package org.orienteer.bpm;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;
import org.orienteer.bpm.camunda.BpmnHook;
import org.orienteer.bpm.camunda.OProcessApplication;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.method.OMethodsManager;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * {@link IOrienteerModule} for 'orienteer-bpm' module
 */
public class BPMModule extends AbstractOrienteerModule{
	
	private static final Logger LOG = LoggerFactory.getLogger(BPMModule.class);

	private ProcessApplicationReference processApplicationReference;
	
	protected BPMModule() {
		super("bpm", 2, "devutils");
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		helper.oAbstractClass(IEntityHandler.BPM_ENTITY_CLASS)
			  	.oProperty("id", OType.STRING, 0)
			  		.updateCustomAttribute(CustomAttribute.UI_READONLY, true)
			  		.oIndex(INDEX_TYPE.UNIQUE);
		HandlersManager.get().applySchema(helper);
		return null;
	}
	
	@Override
	public void onUpdate(OrienteerWebApplication app, ODatabaseSession db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		onInstall(app, db);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseSession db) {
		super.onInitialize(app, db);
		app.mountPackage("org.orienteer.bpm.web");

		app.registerWidgets("org.orienteer.bpm.component.widget");
		
		app.getOrientDbSettings().addORecordHooks(BpmnHook.class);
		OMethodsManager.get().addModule(BPMModule.class);

		processApplicationReference = deployApplication();
	}

	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseSession db) {
		super.onDestroy(app, db);
		OMethodsManager.get().removeModule(BPMModule.class);
		app.unregisterWidgets("org.orienteer.bpm.component.widget");
		app.unmountPackage("org.orienteer.bpm.web");
		app.getOrientDbSettings().removeORecordHooks(BpmnHook.class);

		if (processApplicationReference != null) {
			undeployApplication(processApplicationReference);
		}
	}


	private ProcessApplicationReference deployApplication() {
		FutureTask<ProcessApplicationReference> task = new FutureTask<>(() -> {
			OProcessApplication processApplication = new OProcessApplication();
			processApplication.deploy();
			return processApplication.getReference();
		});

		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(task);
		try {
			return task.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	private void undeployApplication(ProcessApplicationReference processApplicationReference) {
		try {
			CompletableFuture.runAsync(() -> {
				try {
					processApplicationReference.getProcessApplication().undeploy();
				} catch (ProcessApplicationUnavailableException e) {
					LOG.error("Can't undeploy process application", e);
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}
}
