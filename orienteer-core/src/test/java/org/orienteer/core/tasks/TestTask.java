package org.orienteer.core.tasks;

import org.orienteer.core.util.OSchemaHelper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TestTask extends OTask {
	
		public static final String TASK_CLASS = "TestTask";
		
		public static final int PROGRESS = 100;
		public static final long PROGRESS_CURRENT = 10;
		public static final long PROGRESS_FINAL = 20;
		
		/**
		 * Register fields in db 
		 */
		public static void init(ODatabaseDocument db){
			OSchemaHelper helper = OSchemaHelper.bind(db);
			helper.oClass(TASK_CLASS,OTask.TASK_CLASS);
			setOTaskJavaClassName(db,TASK_CLASS,"org.orienteer.core.tasks.TestTask");
		}	
		
		public static void close(ODatabaseDocument db){
			db.getMetadata().getSchema().dropClass(TASK_CLASS);
		}
		
		public TestTask(ODocument oTask) {
			super(oTask);
		}

		@Override
		public OTaskSession<?> startNewSession() {
			final OTaskSessionImpl otaskSession = new OTaskSessionImpl();
			otaskSession.onStart(this).
				setDeleteOnFinish((boolean) getField(OTask.Field.AUTODELETE_SESSIONS)).
				setFinalProgress(PROGRESS_FINAL).
			end();
			for (int i = 0; i < PROGRESS_CURRENT; i++) {
				otaskSession.onProcess().
					incrementCurrentProgress().
					setProgress(PROGRESS).
				end();
			}
			otaskSession.onStop();
			return otaskSession;		
		}
		//////////////////////////////////////////////////////////////////////
	}