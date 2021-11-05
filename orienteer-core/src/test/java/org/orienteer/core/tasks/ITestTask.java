package org.orienteer.core.tasks;

import org.orienteer.core.dao.DAO;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.transponder.annotation.EntityType;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.db.ODatabaseSession;

@ProvidedBy(ODocumentWrapperProvider.class)
@EntityType(value = ITestTask.CLASS_NAME, orderOffset = 50)
public interface ITestTask extends IOTask<IOTaskSessionPersisted> {
	
		public static final String CLASS_NAME = "TestTask";
		
		public static final double PROGRESS = 100;
		public static final double PROGRESS_CURRENT = 10;
		public static final double PROGRESS_FINAL = 20;
		
		@Override
		public default OTaskSessionRuntime<IOTaskSessionPersisted> startNewSession() {
			final OTaskSessionRuntime<IOTaskSessionPersisted> otaskSession = OTaskSessionRuntime.simpleSession(this);
			otaskSession
				.setFinalProgress(PROGRESS_FINAL)
				.start();
			for (int i = 0; i < PROGRESS_CURRENT; i++) {
				otaskSession.incrementCurrentProgress();
			}
			otaskSession.finish();
			return otaskSession;		
		}
		
		public static void init(ODatabaseSession session) {
			DAO.define(ITestTask.class);
		}
		
		public static void close(ODatabaseSession session) {
			session.getMetadata().getSchema().dropClass(CLASS_NAME);
		}
	}