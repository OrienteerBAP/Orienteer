package org.orienteer.core.dao;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class GuiceTest {

	@Inject
	private OrienteerTester tester;
	
	@Test
	public void testInjection() {
		IOPerspective iOPerspective = tester.getApplication().getServiceInstance(IOPerspective.class);
		List<ODocument> perspectives = tester.getDatabase().query(new OSQLSynchQuery<ODocument>("select from OPerspective"));
		for (ODocument oDocument : perspectives) {
			iOPerspective.fromStream(oDocument);
			assertEquals(oDocument.field("alias"), iOPerspective.getAlias());
			assertEquals(oDocument.field("alias"), iOPerspective.getAliasSynonymMethod());
			assertEquals("test"+oDocument.field("alias"), iOPerspective.getTestAlias());
			assertEquals("test2"+oDocument.field("alias"), iOPerspective.getTest2Alias());
			assertEquals("test3test"+oDocument.field("alias"), iOPerspective.getTest3Alias());
			assertEquals((Object)oDocument.field("alias"), iOPerspective.getDocument().field("alias"));
		}
	}
	
	@Test
	public void testLookups() {
		IOPerspective iOPerspective = tester.getApplication().getServiceInstance(IOPerspective.class);
		assertTrue(iOPerspective.lookupToBoolean("default"));
		assertEquals("default", iOPerspective.getAlias());
		assertEquals("testdefault", iOPerspective.getTestAlias());
		assertEquals("test2default", iOPerspective.getTest2Alias());
		assertEquals("test3testdefault", iOPerspective.getTest3Alias());
		IOPerspective other = iOPerspective.lookupAsChain("default");
		assertSame(iOPerspective, other);
		assertNull(iOPerspective.lookupAsChain("notExistingPerspective"));
	}
	
	@Test
	public void testQuery() {
		IOPerspective iOPerspective = tester.getApplication().getServiceInstance(IOPerspective.class);
		iOPerspective.lookupToBoolean("default");
		List<ODocument> menu = iOPerspective.listAllMenu();
		assertNotNull(menu);
		assertTrue("Size of menu", menu.size()>0);
	}
	
	@Test
	public void testDAO() {
		ITestDAO dao = tester.getApplication().getServiceInstance(ITestDAO.class);
		List<ODocument> perspectives = dao.listOPerspective();
		assertNotNull(perspectives);
		assertTrue("Size of perspectives", perspectives.size()>0);
		assertTrue("Size of perspectives", dao.countPerspectives()>0);
		assertEquals(perspectives.size(), dao.countPerspectives());
	}
	
	@Test
	public void testMirroring() {
		IOPerspective iOPerspective = tester.getApplication().getServiceInstance(IOPerspective.class);
		iOPerspective.lookupToBoolean("default");
		assertNotNull(iOPerspective.getDocument());
		Object reloadRet = iOPerspective.reload();
		assertTrue(reloadRet == iOPerspective);
	}
	
	@Test
	public void testConvertions() {
		ITestDAO dao = tester.getApplication().getServiceInstance(ITestDAO.class);
		ODocument doc = dao.findSingleAsDocument("default");
		IOPerspective pers = dao.findSingleAsDAO("default");
		assertEquals(doc.field("alias"), pers.getAlias());
		List<ODocument> listDocs = dao.findAllAsDocument();
		List<IOPerspective> listObjs = dao.findAllAsDAO();
		assertEquals(listDocs.size(), listObjs.size());
		assertTrue(listDocs.get(0) instanceof ODocument);
		assertTrue(listObjs.get(0) instanceof IOPerspective);
	}
}
