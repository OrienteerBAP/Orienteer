package org.orienteer.core;

import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.ITaskSession.Status;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.core.tasks.TestTask;
import org.orienteer.core.tasks.console.OConsoleTask;
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
	public void testSimpleSession() throws Exception {
		ITaskSession session = new OTaskSessionRuntime();
		assertEquals(Status.NOT_STARTED, session.getStatus());
		assertEquals(Status.DETACHED, session.getOTaskSessionPersisted().getStatus());
		assertNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
		assertNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
		session.start();
		assertEquals(Status.RUNNING, session.getStatus());
		assertEquals(Status.RUNNING, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
		assertNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
		assertFalse(session.isInterruptable());
		session.finish();
		assertEquals(Status.FINISHED, session.getStatus());
		assertEquals(Status.FINISHED, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
		assertNotNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
	}
	
	@Test
	public void testThreadedSession() throws Exception {
		final ITaskSession session = new OTaskSessionRuntime();
		assertEquals(Status.NOT_STARTED, session.getStatus());
		assertEquals(Status.DETACHED, session.getOTaskSessionPersisted().getStatus());
		assertNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
		assertNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				session.start();
				try { Thread.sleep(250);} catch (InterruptedException e) {}
				session.finish();
			}
		}).start();
		try { Thread.sleep(100);} catch (InterruptedException e) {}
		assertEquals(Status.RUNNING, session.getStatus());
		assertEquals(Status.RUNNING, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
		assertNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
		assertFalse(session.isInterruptable());
		try { Thread.sleep(250);} catch (InterruptedException e) {}
		assertEquals(Status.FINISHED, session.getStatus());
		assertEquals(Status.FINISHED, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
		assertNotNull(session.getOTaskSessionPersisted().getDocument().field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
	}
	
	@Test
	@Ignore
	public void taskTestAndTaskSessionTest() throws Exception{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		assertFalse(db.isClosed());
		db.commit();

		TestTask.init(db);
		
		try{
			ODocument taskDocument = new ODocument(TestTask.TASK_CLASS);
			taskDocument.field(OTask.Field.AUTODELETE_SESSIONS.fieldName(),false);
			taskDocument.save();
			db.commit();
			
			OTask task = OTask.makeFromODocument(taskDocument);
			OTaskSessionRuntime taskSession = task.startNewSession();
			
			ODocument taskSessionDoc = taskSession.getOTaskSessionPersisted().getDocument();

			assertNotNull(taskSessionDoc.field(ITaskSession.Field.THREAD_NAME.fieldName()));
			assertEquals(ITaskSession.Status.FINISHED,taskSession.getStatus());
			assertNotNull(taskSessionDoc.field(ITaskSession.Field.START_TIMESTAMP.fieldName()));
			assertNotNull(taskSessionDoc.field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
			assertEquals((int)TestTask.PROGRESS,taskSessionDoc.field(ITaskSession.Field.PROGRESS.fieldName()));
			assertEquals((long)TestTask.PROGRESS_CURRENT,taskSessionDoc.field(ITaskSession.Field.PROGRESS_CURRENT.fieldName()));
			assertEquals((long)TestTask.PROGRESS_FINAL,taskSessionDoc.field(ITaskSession.Field.PROGRESS_FINAL.fieldName()));
			assertEquals(false,taskSessionDoc.field(ITaskSession.Field.IS_STOPPABLE.fieldName()));
			assertEquals(false,taskSessionDoc.field(ITaskSession.Field.DELETE_ON_FINISH.fieldName()));
			assertNull(taskSessionDoc.field(ITaskSession.Field.ERROR_TYPE.fieldName()));
			assertNull(taskSessionDoc.field(ITaskSession.Field.ERROR.fieldName()));
		} finally
		{
			TestTask.close(db);
		}
		OrientDbWebSession.get().signOut();
	}
	
	@Test
	@Ignore
	public void consoleTaskTest() throws Exception{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		assertFalse(db.isClosed());
		db.commit();
		try
		{
			ODocument taskDocument = new ODocument(OConsoleTask.TASK_CLASS);
			taskDocument.field(OTask.Field.AUTODELETE_SESSIONS.fieldName(),false);
			taskDocument.field(OConsoleTask.Field.INPUT.fieldName(),CONSOLE_TEST_COMMAND);
			taskDocument.save();
			db.commit();
			OTask task = OTask.makeFromODocument(taskDocument);
	
			ODocument taskDocumentAD = new ODocument(OConsoleTask.TASK_CLASS);
			taskDocumentAD.field(OTask.Field.AUTODELETE_SESSIONS.fieldName(),true);
			taskDocumentAD.field(OConsoleTask.Field.INPUT.fieldName(),CONSOLE_TEST_COMMAND);
			taskDocumentAD.save();
			db.commit();
			OTask taskAD = OTask.makeFromODocument(taskDocumentAD);
			
			OTaskSessionRuntime taskSession = task.startNewSession();
			OTaskSessionRuntime taskSessionAD = taskAD.startNewSession();
			Thread.sleep(CONSOLE_TASK_DELAY);
			db.commit();
			ODocument taskSessionDoc = taskSession.getOTaskSessionPersisted().getDocument();
			taskSessionDoc.load();
			assertNull(taskSessionDoc.field(ITaskSession.Field.ERROR.fieldName()));
			assertNotNull(taskSessionDoc.field(ITaskSession.Field.FINISH_TIMESTAMP.fieldName()));
			assertEquals((long)12,taskSessionDoc.field(ITaskSession.Field.PROGRESS_CURRENT.fieldName()));
			assertEquals(ITaskSession.Status.INTERRUPTED,taskSession.getStatus());
		} finally
		{
			OrientDbWebSession.get().signOut();
		}
	}
	
}
