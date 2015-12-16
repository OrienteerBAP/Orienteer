package org.orienteer.pages.module;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.pages.PagesCompoundRequestMapper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link ORecordHook} to keep pages and mounts in sync
 */
public class PagesHook extends ODocumentHookAbstract {
	
	public PagesHook(ODatabaseDocument database) {
		super(database);
		setIncludeClasses(PagesModule.OCLASS_PAGE);
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}

	@Override
	public void onRecordAfterCreate(ODocument iDocument) {
		getPagesCompoundRequestMapper().add(iDocument);
	}

	@Override
	public void onRecordAfterUpdate(ODocument iDocument) {
		PagesCompoundRequestMapper mapper = getPagesCompoundRequestMapper();
		mapper.remove(iDocument);
		mapper.add(iDocument);
	}

	@Override
	public void onRecordAfterDelete(ODocument iDocument) {
		getPagesCompoundRequestMapper().remove(iDocument);
	}
	
	protected PagesCompoundRequestMapper getPagesCompoundRequestMapper() {
		return OrienteerWebApplication.get().getServiceInstance(PagesModule.class).getPagesCompoundRequestMapper();
	}
	
	

}
