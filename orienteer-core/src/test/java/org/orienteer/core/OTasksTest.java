package org.orienteer.core;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.orienteer.core.tasks.OConsoleTask;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSession;
import org.orienteer.core.tasks.OTaskSessionUpdater;
import org.orienteer.core.tasks.TestTask;
import org.orienteer.junit.OrienteerTestRunner;
import static org.junit.Assert.*;

import java.util.ArrayList;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

@RunWith(OrienteerTestRunner.class)
@Singleton

public class OTasksTest {
	static final private String CONSOLE_TEST_COMMAND = "ping 127.0.0.1";
	static final private int CONSOLE_TASK_DELAY = 5000;

	@Test
	public void taskTestAndTaskSessionTest() throws Exception{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		assertFalse(db.isClosed());
		db.commit();

		TestTask.init(db);
		ArrayList<String> created = new ArrayList<String>();
		
		try{
			ODocument taskDocument = new ODocument(TestTask.TASK_CLASS);
			taskDocument.field(OTask.Field.AUTODELETE_SESSIONS.fieldName(),false);
			taskDocument.save();
			db.commit();
			created.add(taskDocument.getIdentity().toString());
			
			OTask task = OTask.makeFromODocument(taskDocument);
			OTaskSession<?> taskSession = task.startNewSession();
			created.add(taskSession.getId());
			
			Thread.sleep(OTaskSessionUpdater.WRITE_DELAY_MAX*2);
			db.commit();

			
			ODocument taskSessionDoc = new ODocument(new ORecordId(taskSession.getId()));
			taskSessionDoc.load();

			assertNotNull(taskSessionDoc.field(OTaskSession.Field.THREAD_NAME.fieldName()));
			assertEquals(OTaskSession.Status.STOPPED.name(),taskSessionDoc.field(OTaskSession.Field.STATUS.fieldName()));
			assertEquals(taskDocument.getIdentity().toString(),((OIdentifiable)taskSessionDoc.field(OTaskSession.Field.TASK_LINK.fieldName())).getIdentity().toString());
			assertNotNull(taskSessionDoc.field(OTaskSession.Field.START_TIMESTAMP.fieldName()));
			assertNotNull(taskSessionDoc.field(OTaskSession.Field.FINISH_TIMESTAMP.fieldName()));
			assertEquals((int)TestTask.PROGRESS,taskSessionDoc.field(OTaskSession.Field.PROGRESS.fieldName()));
			assertEquals((long)TestTask.PROGRESS_CURRENT,taskSessionDoc.field(OTaskSession.Field.PROGRESS_CURRENT.fieldName()));
			assertEquals((long)TestTask.PROGRESS_FINAL,taskSessionDoc.field(OTaskSession.Field.PROGRESS_FINAL.fieldName()));
			assertEquals(false,taskSessionDoc.field(OTaskSession.Field.IS_STOPPABLE.fieldName()));
			assertEquals(false,taskSessionDoc.field(OTaskSession.Field.DELETE_ON_FINISH.fieldName()));
			assertNull(taskSessionDoc.field(OTaskSession.Field.ERROR_TYPE.fieldName()));
			assertNull(taskSessionDoc.field(OTaskSession.Field.ERROR.fieldName()));
		} finally
		{
			for (String id : created) {
				ODocument curDoc = new ODocument(new ORecordId(id));
				try {
					curDoc.delete();
				} catch (ORecordNotFoundException e) {
				}
			}
			
			TestTask.close(db);
		}
		OrientDbWebSession.get().signOut();
	}
	
	@Test
	public void consoleTaskTest() throws Exception{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		assertFalse(db.isClosed());
		db.commit();
		ArrayList<String> created = new ArrayList<String>();
		try
		{
			ODocument taskDocument = new ODocument(OConsoleTask.TASK_CLASS);
			taskDocument.field(OTask.Field.AUTODELETE_SESSIONS.fieldName(),false);
			taskDocument.field(OConsoleTask.Field.INPUT.fieldName(),CONSOLE_TEST_COMMAND);
			taskDocument.save();
			db.commit();
			created.add(taskDocument.getIdentity().toString());
			OTask task = OTask.makeFromODocument(taskDocument);
	
			ODocument taskDocumentAD = new ODocument(OConsoleTask.TASK_CLASS);
			taskDocumentAD.field(OTask.Field.AUTODELETE_SESSIONS.fieldName(),true);
			taskDocumentAD.field(OConsoleTask.Field.INPUT.fieldName(),CONSOLE_TEST_COMMAND);
			taskDocumentAD.save();
			db.commit();
			created.add(taskDocumentAD.getIdentity().toString());
			OTask taskAD = OTask.makeFromODocument(taskDocumentAD);
			
			OTaskSession<?> taskSession = task.startNewSession();
			created.add(taskSession.getId());
			OTaskSession<?> taskSessionAD = taskAD.startNewSession();
			Thread.sleep(CONSOLE_TASK_DELAY);
			db.commit();
			ODocument taskSessionDocAD = new ODocument(new ORecordId(taskSessionAD.getId()));
			try {
				taskSessionDocAD.load("*:1",true);
				fail("Task session autodeleting does not work!");
				created.add(taskSessionAD.getId());
			} catch (ORecordNotFoundException e) {
			}
			ODocument taskSessionDoc = new ODocument(new ORecordId(taskSession.getId()));
			taskSessionDoc.load();
			assertNull(taskSessionDoc.field(OTaskSession.Field.ERROR.fieldName()));
			assertNotNull(taskSessionDoc.field(OTaskSession.Field.FINISH_TIMESTAMP.fieldName()));
			assertEquals((long)12,taskSessionDoc.field(OTaskSession.Field.PROGRESS_CURRENT.fieldName()));
			assertEquals(OTaskSession.Status.STOPPED.name(),taskSessionDoc.field(OTaskSession.Field.STATUS.fieldName()));
			assertEquals(taskDocument.getIdentity().toString(),((OIdentifiable)taskSessionDoc.field(OTaskSession.Field.TASK_LINK.fieldName())).getIdentity().toString());
		} finally
		{
			for (String id : created) {
				ODocument curDoc = new ODocument(new ORecordId(id));
				try {
					curDoc.delete();
				} catch (ORecordNotFoundException e) {
				}
			}
			OrientDbWebSession.get().signOut();
		}
	}
	
}
