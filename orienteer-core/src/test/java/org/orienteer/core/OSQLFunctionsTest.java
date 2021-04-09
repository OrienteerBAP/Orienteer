package org.orienteer.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class OSQLFunctionsTest {
	@Inject
	private OrienteerTester tester;
	
	@Test
	@Sudo
	public void testCurrentUser() {
		try(OResultSet rs = tester.getDatabaseSession().query("select expand(ouser())")) {
			assertTrue(rs.hasNext());
			OResult result = rs.next();
			assertNotNull(result);
			Optional<ORecord> user = result.getRecord();
			assertTrue(user.isPresent());
			assertEquals(tester.getSession().getUserAsODocument(), user.get());
		}
	}
}
