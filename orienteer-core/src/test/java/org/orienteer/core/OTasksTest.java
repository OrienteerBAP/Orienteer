package org.orienteer.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.tasks.IOConsoleTask;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.ITaskSession;
import org.orienteer.core.tasks.ITaskSession.Status;
import org.orienteer.core.tasks.ITestTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.junit.OrienteerTestRunner;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

@RunWith(OrienteerTestRunner.class)
@Singleton

public class OTasksTest {
	static final private String CONSOLE_TEST_COMMAND = "ping 127.0.0.1";
	static final private int CONSOLE_TASK_DELAY = 5000;

	
	@Test
	public void testSimpleSession() throws Exception {
		ITaskSession session = OTaskSessionRuntime.simpleSession();
		assertEquals(Status.NOT_STARTED, session.getStatus());
		assertEquals(Status.DETACHED, session.getOTaskSessionPersisted().getStatus());
		IOTaskSessionPersisted persisted = session.getOTaskSessionPersisted();
		assertNull(persisted.getStartTimestamp());
		assertNull(persisted.getFinishTimestamp());
		session.start();
		assertEquals(Status.RUNNING, session.getStatus());
		assertEquals(Status.RUNNING, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(persisted.getStartTimestamp());
		assertNull(persisted.getFinishTimestamp());
		assertFalse(session.isInterruptable());
		session.finish();
		try { Thread.sleep(500);} catch (InterruptedException e) {}
		assertEquals(Status.FINISHED, session.getStatus());
		assertEquals(Status.FINISHED, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(persisted.getStartTimestamp());
		assertNotNull(persisted.getFinishTimestamp());
	}
	
	@Test
	public void testThreadedSession() throws Exception {
		final ITaskSession session = OTaskSessionRuntime.simpleSession();
		assertEquals(Status.NOT_STARTED, session.getStatus());
		IOTaskSessionPersisted persisted = session.getOTaskSessionPersisted();
		assertEquals(Status.DETACHED, session.getOTaskSessionPersisted().getStatus());
		assertNull(persisted.getStartTimestamp());
		assertNull(persisted.getFinishTimestamp());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				session.start();
				try { Thread.sleep(1000);} catch (InterruptedException e) {}
				session.finish();
			}
		}).start();
		try { Thread.sleep(100);} catch (InterruptedException e) {}
		assertEquals(Status.RUNNING, session.getStatus());
		assertEquals(Status.RUNNING, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(persisted.getStartTimestamp());
		assertNull(persisted.getFinishTimestamp());
		assertFalse(session.isInterruptable());
		try { Thread.sleep(2000);} catch (InterruptedException e) {}
		assertEquals(Status.FINISHED, session.getStatus());
		assertEquals(Status.FINISHED, session.getOTaskSessionPersisted().getStatus());
		assertNotNull(persisted.getStartTimestamp());
		assertNotNull(persisted.getFinishTimestamp());
	}
	
	@Test
	@Ignore
	public void taskTestAndTaskSessionTest() throws Exception{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseSession db = OrientDbWebSession.get().getDatabaseSession();
		assertFalse(db.isClosed());
		db.commit();

		ITestTask.init(db);
		
		try{
			ITestTask task = DAO.create(ITestTask.class);
			task.setAutodeleteSessions(false);
			task.save();
			
			OTaskSessionRuntime<IOTaskSessionPersisted> taskSession = task.startNewSession();
			
			IOTaskSessionPersisted persisted = taskSession.getOTaskSessionPersisted();
			
			assertNotNull(persisted.getThreadName());
			assertEquals(ITaskSession.Status.FINISHED,taskSession.getStatus());
			assertNotNull(persisted.getStartTimestamp());
			assertNotNull(persisted.getFinishTimestamp());
			assertEquals(ITestTask.PROGRESS, persisted.getProgress(), 0);
			assertEquals(ITestTask.PROGRESS_CURRENT,persisted.getCurrentProgress(), 0);
			assertEquals(ITestTask.PROGRESS_FINAL,persisted.getFinalProgress(), 0);
			assertEquals(false, persisted.isStopable());
			assertEquals(false, persisted.isDeleteOnFinish());
			assertNull(persisted.getErrorType());
			assertNull(persisted.getError());
		} finally
		{
			ITestTask.close(db);
		}
		OrientDbWebSession.get().signOut();
	}
	
	@Test
	@Ignore
	public void consoleTaskTest() throws Exception{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseSession db = OrientDbWebSession.get().getDatabaseSession();
		assertFalse(db.isClosed());
		db.commit();
		try
		{
			IOConsoleTask task = DAO.create(IOConsoleTask.class);
			task.setAutodeleteSessions(false);
			task.setInput(CONSOLE_TEST_COMMAND);
			task.save();

			IOConsoleTask taskAD = DAO.create(IOConsoleTask.class);
			task.setAutodeleteSessions(true);
			task.setInput(CONSOLE_TEST_COMMAND);
			task.save();
			
			OTaskSessionRuntime<IOTaskSessionPersisted> taskSession = task.startNewSession();
			Thread.sleep(CONSOLE_TASK_DELAY);
			db.commit();
			IOTaskSessionPersisted persisted = taskSession.getOTaskSessionPersisted();
			assertNull(persisted.getError());
			assertNotNull(persisted.getFinishTimestamp());
			assertEquals(12.0, persisted.getCurrentProgress(), 0);
			assertEquals(ITaskSession.Status.INTERRUPTED,taskSession.getStatus());
		} finally
		{
			OrientDbWebSession.get().signOut();
		}
	}
	
}
