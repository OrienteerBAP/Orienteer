package org.orienteer.rproxy;

import org.orienteer.core.dao.DAO;

import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Hook to mount/remount/unmount end-points according to ongoing modifications 
 */
public class ORProxyHook extends ODocumentHookAbstract {

	public ORProxyHook() {
		setIncludeClasses(IORProxyEndPoint.CLASS_NAME);
	}
	
	@Override
	public void onRecordAfterCreate(ODocument iDocument) {
		ORProxyResource.mount(DAO.provide(IORProxyEndPoint.class, iDocument));
	}

	@Override
	public void onRecordAfterUpdate(ODocument iDocument) {
		ORProxyResource.mount(DAO.provide(IORProxyEndPoint.class, iDocument));
	}

	@Override
	public RESULT onRecordBeforeDelete(ODocument iDocument) {
		ORProxyResource.unmount(DAO.provide(IORProxyEndPoint.class, iDocument));
		return RESULT.RECORD_NOT_CHANGED;
	}

	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}

}
