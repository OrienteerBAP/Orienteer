package org.orienteer.bpm;

import org.camunda.bpm.application.ProcessApplicationReference;
import org.camunda.bpm.application.ProcessApplicationUnavailableException;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.orienteer.bpm.camunda.OProcessApplication;
import org.orienteer.core.CustomAttributes;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.AbstractOrienteerModule;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.devutils.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * {@link IOrienteerModule} for 'orienteer-bpm' module
 */
public class BPMModule extends AbstractOrienteerModule{
	
	private static final Logger LOG = LoggerFactory.getLogger(BPMModule.class);

	private ProcessApplicationReference processApplicationReference;
	
	protected BPMModule() {
		super("orienteer-bpm", 1, "devutils");
	}
	
	@Override
	public ODocument onInstall(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInstall(app, db);
		OSchemaHelper helper = OSchemaHelper.bind(db);
		OObjectDatabaseTx odb = new OObjectDatabaseTx((ODatabaseDocumentTx)db);
		OEntityManager em = odb.getEntityManager();
		em.registerEntityClass(DbEntity.class);
		return null;
	}
	
	@Override
	public void onInitialize(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onInitialize(app, db);
		onInstall(app, db);
		app.mountPages("org.orienteer.bpm.web");
		OProcessApplication processApplication = new OProcessApplication();
		processApplication.deploy();
		processApplicationReference = processApplication.getReference();
	}
	
	@Override
	public void onDestroy(OrienteerWebApplication app, ODatabaseDocument db) {
		super.onDestroy(app, db);
		app.unmountPages("org.orienteer.bpm.web");
		if(processApplicationReference!=null) {
			try {
				processApplicationReference.getProcessApplication().undeploy();
			} catch (ProcessApplicationUnavailableException e) {
				LOG.error("Can't undeploy process application", e);
			}
		}
	}
	
}
