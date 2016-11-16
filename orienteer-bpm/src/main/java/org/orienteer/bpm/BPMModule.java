package org.orienteer.bpm;

import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;
import org.orienteer.bpm.camunda.BpmnHook;
import org.orienteer.bpm.camunda.OProcessApplication;
import org.orienteer.bpm.camunda.handler.AbstractEntityHandler;
import org.orienteer.bpm.camunda.handler.HandlersManager;
import org.orienteer.bpm.camunda.handler.IEntityHandler;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

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
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
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
	public void onUpdate(OrienteerWebApplication app, ODatabaseDocument db, int oldVersion, int newVersion) {
		super.onUpdate(app, db, oldVersion, newVersion);
		onInstall(app, db);
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		app.mountPages("org.orienteer.bpm.web");
		OProcessApplication processApplication = new OProcessApplication();
		processApplication.deploy();
		processApplicationReference = processApplication.getReference();
		app.registerWidgets("org.orienteer.bpm.component.widget");
		app.getOrientDbSettings().getORecordHooks().add(BpmnHook.class);
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unregisterWidgets("org.orienteer.bpm.component.widget");
		app.unmountPages("org.orienteer.bpm.web");
		app.getOrientDbSettings().getORecordHooks().remove(BpmnHook.class);
		if(processApplicationReference!=null) {
			try {
				processApplicationReference.getProcessApplication().undeploy();
			} catch (ProcessApplicationUnavailableException e) {
				LOG.error("Can't undeploy process application", e);
			}
		}
	}
	
}
