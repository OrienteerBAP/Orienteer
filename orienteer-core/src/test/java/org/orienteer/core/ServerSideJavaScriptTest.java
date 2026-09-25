package org.orienteer.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * Server-side JavaScript in the embedded OrientDB runs on GraalJS 25.x
 * (OrientDB's own GraalVM 21.3.5 is excluded, see REFRESH_PLAN.md D8)
 */
@RunWith(OrienteerTestRunner.class)
@Singleton
public class ServerSideJavaScriptTest {
	@Inject
	private OrienteerTester tester;
	
	@Test
	@Sudo
	public void testJavaScriptCommand() {
		ODatabaseSession db = tester.getDatabaseSession();
		try(OResultSet rs = db.execute("javascript", "var a = 20; a + 22;")) {
			assertTrue(rs.hasNext());
			assertEquals(42, ((Number) rs.next().getProperty("value")).intValue());
		}
	}
	
	@Test
	@Sudo
	public void testJavaScriptFunction() {
		ODatabaseSession db = tester.getDatabaseSession();
		String name = "testServerSideJavaScriptSum";
		db.command("DELETE FROM OFunction WHERE name = ?", name).close();
		db.command("CREATE FUNCTION "+name+" \"return a + b;\" PARAMETERS [a, b] LANGUAGE javascript").close();
		try(OResultSet rs = db.query("select "+name+"(40, 2) as result")) {
			assertTrue(rs.hasNext());
			assertEquals(42, ((Number) rs.next().getProperty("result")).intValue());
		} finally {
			db.command("DELETE FROM OFunction WHERE name = ?", name).close();
		}
	}
}
