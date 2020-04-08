package org.orienteer.core.dao;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class DAOTest {
	
	static final String TEST_CLASS = "DAOTestClass";

	private static OrienteerTester tester;
	
	static {
		tester = OrienteerWebApplication.lookupApplication().getServiceInstance(OrienteerTester.class);
	}
	
	@BeforeClass
	public static void beforeDAOTest() {
		new DBClosure<Boolean>() {

			@Override
			protected Boolean execute(ODatabaseDocument db) {
				OSchemaHelper helper = OSchemaHelper.bind(db)
						.oClass(TEST_CLASS)
						.oProperty("name", OType.STRING)
						.oProperty("parent", OType.LINK)
						.oProperty("child", OType.LINKLIST)
						.setupRelationship(TEST_CLASS, "parent", TEST_CLASS, "child");
				ODocument root = helper.oDocument()
						.field("name", "root")
						.saveDocument().getODocument();
				for(int i=0; i<5; i++) {
					helper.oDocument()
								.field("name", "Child#"+i)
								.field("parent", root)
								.saveDocument();
				}
				return true;
			}
		}.execute();
	}
	
	@AfterClass
	public static void afterDAOTest() {
		new DBClosure<Boolean>() {

			@Override
			protected Boolean execute(ODatabaseDocument db) {
				db.getMetadata().getSchema().dropClass(TEST_CLASS);
				return true;
			}
		}.execute();
	}
	
	@Test
	public void testInjection() {
		IDAOTestClass doc = tester.getApplication().getServiceInstance(IDAOTestClass.class);
		List<ODocument> perspectives = tester.getDatabase().query(new OSQLSynchQuery<ODocument>("select from DAOTestClass"));
		for (ODocument oDocument : perspectives) {
			doc.fromStream(oDocument);
			assertEquals(oDocument.field("name"), doc.getName());
			assertEquals(oDocument.field("name"), doc.getNameSynonymMethod());
			assertEquals("test"+oDocument.field("name"), doc.getTestName());
			assertEquals("test2"+oDocument.field("name"), doc.getTest2Name());
			assertEquals("test3test"+oDocument.field("name"), doc.getTest3Name());
			assertEquals((Object)oDocument.field("name"), doc.getDocument().field("name"));
		}
	}
	
	@Test
	public void testLookups() {
		IDAOTestClass iOPerspective = tester.getApplication().getServiceInstance(IDAOTestClass.class);
		assertTrue(iOPerspective.lookupToBoolean("root"));
		assertEquals("root", iOPerspective.getName());
		assertEquals("testroot", iOPerspective.getTestName());
		assertEquals("test2root", iOPerspective.getTest2Name());
		assertEquals("test3testroot", iOPerspective.getTest3Name());
		IDAOTestClass other = iOPerspective.lookupAsChain("root");
		assertSame(iOPerspective, other);
		assertNull(iOPerspective.lookupAsChain("notExistingPerspective"));
	}
	
	@Test
	public void testQuery() {
		IDAOTestClass iOPerspective = tester.getApplication().getServiceInstance(IDAOTestClass.class);
		iOPerspective.lookupToBoolean("root");
		List<ODocument> menu = iOPerspective.listAllChild();
		assertNotNull(menu);
		assertTrue("Size of childs", menu.size()>0);
	}
	
	@Test
	public void testDAO() {
		ITestDAO dao = tester.getApplication().getServiceInstance(ITestDAO.class);
		List<ODocument> testDocs = dao.listDAOTestClass();
		assertNotNull(testDocs);
		assertTrue("Size of test docs", testDocs.size()>0);
		assertTrue("Size of test docs", dao.countAll()>0);
		assertEquals(testDocs.size(), dao.countAll());
	}
	
	@Test
	public void testMirroring() {
		IDAOTestClass doc = tester.getApplication().getServiceInstance(IDAOTestClass.class);
		doc.lookupToBoolean("root");
		assertNotNull(doc.getDocument());
		Object reloadRet = doc.reload();
		assertTrue(reloadRet == doc);
	}
	
	@Test
	public void testConvertions() {
		ITestDAO dao = tester.getApplication().getServiceInstance(ITestDAO.class);
		ODocument doc = dao.findSingleAsDocument("root");
		IDAOTestClass pers = dao.findSingleAsDAO("root");
		assertEquals(doc.field("name"), pers.getName());
		List<ODocument> listDocs = dao.findAllAsDocument();
		List<IDAOTestClass> listObjs = dao.findAllAsDAO();
		assertEquals(listDocs.size(), listObjs.size());
		assertTrue(listDocs.get(0) instanceof ODocument);
		assertTrue(listObjs.get(0) instanceof IDAOTestClass);
	}
	
	@Test
	@Sudo
	public void testDescriber() {
		OSchema schema = tester.getMetadata().getSchema();
		try {
			DAO.describe(OSchemaHelper.bind(tester.getDatabase()), IDAOTestClassA.class);
			assertTrue(schema.existsClass("DAOTestClassRoot"));
			assertTrue(schema.existsClass("DAOTestClassA"));
			assertTrue(schema.existsClass("DAOTestClassB"));
			OClass daoTestClassRoot = schema.getClass("DAOTestClassRoot");
			OClass daoTestClassA = schema.getClass("DAOTestClassA");
			OClass daoTestClassB = schema.getClass("DAOTestClassB");
			
			assertTrue(daoTestClassRoot.isAbstract());
			assertProperty(daoTestClassRoot, "root", OType.STRING);
			
			OProperty root = assertProperty(daoTestClassA, "root", OType.STRING);
			assertEquals("DAOTestClassRoot.root", root.getFullName());
			
			assertProperty(daoTestClassA, "name", OType.STRING);
			assertProperty(daoTestClassA, "bSingle", OType.LINK, daoTestClassB, null);
			assertProperty(daoTestClassA, "bOtherField", OType.LINK, daoTestClassB, null);
			assertProperty(daoTestClassA, "selfType", OType.LINK, daoTestClassA, null);
			assertProperty(daoTestClassA, "linkAsDoc", OType.LINK, daoTestClassB, null);
			assertProperty(daoTestClassA, "embeddedStringList", OType.EMBEDDEDLIST, OType.STRING);
			assertProperty(daoTestClassA, "linkList", OType.LINKLIST, daoTestClassB, null);
			
			assertProperty(daoTestClassB, "alias", OType.STRING);
			assertProperty(daoTestClassB, "linkToA", OType.LINK, daoTestClassA, null);
		} finally {
			if(schema.existsClass("DAOTestClassA")) schema.dropClass("DAOTestClassA");
			if(schema.existsClass("DAOTestClassB")) schema.dropClass("DAOTestClassB");
			if(schema.existsClass("DAOTestClassRoot")) schema.dropClass("DAOTestClassRoot");
		}
	}
	
	@Test
	@Sudo
	public void testDescribeAllTypes() {
		OSchema schema = tester.getMetadata().getSchema();
		try {
			DAO.describe(OSchemaHelper.bind(tester.getDatabase()), IDAOAllTypesTestClass.class);
			assertTrue(schema.existsClass("DAOAllTypesTestClass"));
			assertTrue(schema.existsClass("IDAODummyClass"));
			OClass oClass = schema.getClass("DAOAllTypesTestClass");
			OClass dummyClass = schema.getClass("IDAODummyClass");
			
			assertTrue(!oClass.isAbstract());
			
			assertProperty(oClass, "boolean", OType.BOOLEAN, false);
			assertProperty(oClass, "booleanPrimitive", OType.BOOLEAN, true);
			assertProperty(oClass, "booleanDeclared", OType.BOOLEAN, true);
			
			assertProperty(oClass, "integer", OType.INTEGER);
			assertProperty(oClass, "short", OType.SHORT);
			assertProperty(oClass, "long", OType.LONG);
			assertProperty(oClass, "float", OType.FLOAT);
			assertProperty(oClass, "double", OType.DOUBLE);
			assertProperty(oClass, "dateTime", OType.DATETIME);
			assertProperty(oClass, "date", OType.DATE);
			assertProperty(oClass, "string", OType.STRING);
			assertProperty(oClass, "binary", OType.BINARY);
			assertProperty(oClass, "decimal", OType.DECIMAL);
			assertProperty(oClass, "byte", OType.BYTE);
			assertProperty(oClass, "custom", OType.CUSTOM);
			assertProperty(oClass, "transient", OType.TRANSIENT);
			assertProperty(oClass, "any", OType.ANY);
			
			assertProperty(oClass, "link", OType.LINK, dummyClass, null);
			assertProperty(oClass, "linkList", OType.LINKLIST, dummyClass, null);
			assertProperty(oClass, "linkSet", OType.LINKSET, dummyClass, null);
			assertProperty(oClass, "linkMap", OType.LINKMAP, dummyClass, null);
			assertProperty(oClass, "linkBag", OType.LINKBAG, dummyClass, null);
			
			assertProperty(oClass, "embedded", OType.EMBEDDED, dummyClass, null);
			assertProperty(oClass, "embeddedList", OType.EMBEDDEDLIST, dummyClass, null);
			assertProperty(oClass, "embeddedSet", OType.EMBEDDEDSET, dummyClass, null);
			assertProperty(oClass, "embeddedMap", OType.EMBEDDEDMAP, dummyClass, null);
			
			assertProperty(oClass, "embeddedStringSet", OType.EMBEDDEDSET, OType.STRING);
			assertProperty(oClass, "embeddedStringList", OType.EMBEDDEDLIST, OType.STRING);
			assertProperty(oClass, "embeddedStringMap", OType.EMBEDDEDMAP, OType.STRING);

			assertProperty(oClass, "docs", OType.LINKLIST, dummyClass, null);

		} finally {
			if(schema.existsClass("DAOAllTypesTestClass")) schema.dropClass("DAOAllTypesTestClass");
			if(schema.existsClass("IDAODummyClass")) schema.dropClass("IDAODummyClass");
		}
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, OClass linkedClass, String inverse) {
		OProperty prop = assertProperty(oClass, property, oType);
		assertEquals(linkedClass, prop.getLinkedClass());
		assertEquals(inverse, CustomAttribute.PROP_INVERSE.getValue(prop));
		return prop;
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, OType linkedType) {
		OProperty prop = assertProperty(oClass, property, oType);
		assertEquals(linkedType, prop.getLinkedType());
		return prop;
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, boolean notNull) {
		OProperty prop = assertProperty(oClass, property, oType);
		assertEquals(notNull, prop.isNotNull());
		return prop;
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType) {
		OProperty prop = oClass.getProperty(property);
		assertNotNull("Property was not found", prop);
		assertEquals(oType, prop.getType());
		return prop;
	}
}
