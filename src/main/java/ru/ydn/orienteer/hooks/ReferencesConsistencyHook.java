package ru.ydn.orienteer.hooks;

import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ReferencesConsistencyHook extends ODocumentHookAbstract
{
	@Override
	public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
		return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
	}

	@Override
	public void onRecordAfterCreate(ODocument iDocument) {
		// TODO Auto-generated method stub
		super.onRecordAfterCreate(iDocument);
	}

	@Override
	public void onRecordAfterUpdate(ODocument iDocument) {
		// TODO Auto-generated method stub
		super.onRecordAfterUpdate(iDocument);
	}

	@Override
	public void onRecordAfterDelete(ODocument iDocument) {
		// TODO Auto-generated method stub
		super.onRecordAfterDelete(iDocument);
	}
	
	
	
	
}
