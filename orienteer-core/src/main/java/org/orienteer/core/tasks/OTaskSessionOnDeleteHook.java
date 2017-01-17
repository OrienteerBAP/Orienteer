package org.orienteer.core.tasks;

import java.util.Set;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.hook.ORecordHook.HOOK_POSITION;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.security.OSecurityManager;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.components.IHookPosition;

/**
 * Hook for {@link OTaskSession} 
 *
 */
public class OTaskSessionOnDeleteHook extends ODocumentHookAbstract implements IHookPosition {

		
		public OTaskSessionOnDeleteHook(ODatabaseDocument database){
			super(database);
			setIncludeClasses(OTaskSession.TASK_SESSION_CLASS);
		}
		
		@Override
		public void onRecordAfterDelete(ODocument iDocument) {
			OIdentifiable session = iDocument.field( OTaskSession.Field.TASK_LINK.fieldName() );
			if (session!=null){
				ODocument sessionDoc = ((ODocument)session.getRecord());
				if(sessionDoc!=null){
					Set<OIdentifiable> sessions = sessionDoc.field(OTask.Field.SESSIONS.fieldName());
					if (sessions!=null){
						sessions.remove(iDocument);
					}
				}
			}
			super.onRecordAfterDelete(iDocument);
		}
		
		@Override
		public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
			return DISTRIBUTED_EXECUTION_MODE.TARGET_NODE;
		}

		@Override
		public HOOK_POSITION getPosition() {
			return HOOK_POSITION.FIRST;
		}
	}
