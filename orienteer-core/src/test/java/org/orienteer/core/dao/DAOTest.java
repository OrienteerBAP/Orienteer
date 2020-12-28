package org.orienteer.core.dao;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.CustomAttribute;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;

import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

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
			protected Boolean execute(ODatabaseSession db) {
				OSchemaHelper helper = OSchemaHelper.bind(db)
						.oClass(TEST_CLASS)
						.oProperty("name", OType.STRING)
						.oProperty("parent", OType.LINK)
						.oProperty("child", OType.LINKLIST)
						.oProperty("linkMap", OType.LINKMAP)
						.setupRelationship(TEST_CLASS, "parent", TEST_CLASS, "child")
						.setupRelationship(TEST_CLASS, "linkMap", TEST_CLASS);
				ODocument root = helper.oDocument()
						.field("name", "root")
						.saveDocument().getODocument();
				Map<String, ODocument> linkMap = new HashMap<String, ODocument>();
				for(int i=0; i<5; i++) {
					String name = "Child#"+i;
					helper.oDocument()
								.field("name", name)
								.field("parent", root)
								.saveDocument();
					linkMap.put(name, helper.getODocument());
				}
				root.field("linkMap", linkMap);
				root.save();
				return true;
			}
		}.execute();
	}
	
	@AfterClass
	public static void afterDAOTest() {
		new DBClosure<Boolean>() {

			@Override
			protected Boolean execute(ODatabaseSession db) {
				db.getMetadata().getSchema().dropClass(TEST_CLASS);
				return true;
			}
		}.execute();
	}
	
	@Before
	public void makeSureThatDBInThecurrentThread() {
		tester.getDatabaseSession().activateOnCurrentThread();
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
	@Sudo
	public void testConvertions() {
		ITestDAO dao = tester.getApplication().getServiceInstance(ITestDAO.class);
		ODocument doc = dao.findSingleAsDocument("root");
		IDAOTestClass root = dao.findSingleAsDAO("root");
		assertEquals(doc.field("name"), root.getName());
		List<ODocument> listDocs = dao.findAllAsDocument();
		List<IDAOTestClass> listObjs = dao.findAllAsDAO();
		assertEquals(listDocs.size(), listObjs.size());
		assertTrue(listDocs.get(0) instanceof ODocument);
		assertTrue(listObjs.get(0) instanceof IDAOTestClass);
		
		assertConsistent(root.getChild(), root.getChildAsDocuments(), 5);
		
		List<ODocument> allExceptOneChild = root.getChildAsDocuments();
		allExceptOneChild.remove(0);
		root.setChildAsDocuments(allExceptOneChild);
		root.save();
		
		assertConsistent(root.getChild(), root.getChildAsDocuments(), 4);
		
		List<IDAOTestClass> allExceptOneMoreChild = new ArrayList<IDAOTestClass>(root.getChild());
		allExceptOneMoreChild.remove(0);
		root.setChild(allExceptOneMoreChild);
		root.save();
		
		assertConsistent(root.getChild(), root.getChildAsDocuments(), 3);
		
		assertConsistent(root.getLinkMap(), root.getLinkMapAsDocuments(), 5);
		
		Map<String, ODocument> mapDocs = root.getLinkMapAsDocuments();
//		Map<String, ODocument> mapDocs = new HashMap<String, ODocument>(root.getLinkMapAsDocuments());
		Iterator<Map.Entry<String, ODocument>> itDocs = mapDocs.entrySet().iterator();
		itDocs.next();
		itDocs.remove();
		root.setLinkMapAsDocuments(mapDocs);
		root.save();
		
		assertConsistent(root.getLinkMap(), root.getLinkMapAsDocuments(), 4);
		
		
		Map<String, IDAOTestClass> map = new HashMap<String, IDAOTestClass>(root.getLinkMap());
		Iterator<Map.Entry<String, IDAOTestClass>> it = map.entrySet().iterator();
		it.next();
		it.remove();
		root.setLinkMap(map);
		root.save();
		
		assertConsistent(root.getLinkMap(), root.getLinkMapAsDocuments(), 3);
		
	}
	
	private void assertConsistent(List<IDAOTestClass> child, List<ODocument> childAsDoc, int size) {
		assertEquals(child.size(), childAsDoc.size());
		assertThat(child, hasSize(size));
		assertThat(childAsDoc, hasSize(size));
		
		assertThat(child, everyItem(isA(IDAOTestClass.class)));
		assertThat(childAsDoc, everyItem(isA(ODocument.class)));
	}
	
	private void assertConsistent(Map<String, IDAOTestClass> map, Map<String, ODocument> mapOfDocs, int size) {
		assertThat(map, aMapWithSize(size));
		assertThat(map.keySet(), everyItem(isA(String.class)));
		assertThat(map.values(), everyItem(isA(IDAOTestClass.class)));
		
		assertThat(mapOfDocs, aMapWithSize(size));
		assertThat(mapOfDocs.keySet(), everyItem(isA(String.class)));
		assertThat(mapOfDocs.values(), everyItem(isA(ODocument.class)));
	}
	
	@Test
	@Sudo
	public void testDescriber() {
		OSchema schema = tester.getMetadata().getSchema();
		try {
			DAO.describe(OSchemaHelper.bind(tester.getDatabaseSession()), IDAOTestClassA.class);
			assertTrue(schema.existsClass("DAOTestClassRoot"));
			assertTrue(schema.existsClass("DAOTestClassA"));
			assertTrue(schema.existsClass("DAOTestClassB"));
			OClass daoTestClassRoot = schema.getClass("DAOTestClassRoot");
			OClass daoTestClassA = schema.getClass("DAOTestClassA");
			OClass daoTestClassB = schema.getClass("DAOTestClassB");
			
			assertEquals(IDAOTestClassRoot.class.getName(), CustomAttribute.DAO_CLASS.getValue(daoTestClassRoot));
			assertEquals(IDAOTestClassA.class.getName(), CustomAttribute.DAO_CLASS.getValue(daoTestClassA));
			assertEquals(IDAOTestClassB.class.getName(), CustomAttribute.DAO_CLASS.getValue(daoTestClassB));
			
			assertTrue(daoTestClassRoot.isAbstract());
			assertProperty(daoTestClassRoot, "root", OType.STRING, 0);
			
			OProperty root = assertProperty(daoTestClassA, "root", OType.STRING, 0);
			assertEquals("DAOTestClassRoot.root", root.getFullName());
			
			assertProperty(daoTestClassA, "name", OType.STRING, 0);
			assertProperty(daoTestClassA, "bSingle", OType.LINK, 10, daoTestClassB, null);
			assertProperty(daoTestClassA, "bOtherField", OType.LINK, 20, daoTestClassB, null);
			assertProperty(daoTestClassA, "selfType", OType.LINK, 60, daoTestClassA, null);
			assertProperty(daoTestClassA, "linkAsDoc", OType.LINK, 40, daoTestClassB, null);
			assertProperty(daoTestClassA, "embeddedStringList", OType.EMBEDDEDLIST, 30, OType.STRING);
			assertProperty(daoTestClassA, "linkList", OType.LINKLIST, 50, daoTestClassB, null);
			
			assertProperty(daoTestClassB, "alias", OType.STRING, 0);
			assertProperty(daoTestClassB, "linkToA", OType.LINK, 10, daoTestClassA, null);
		} finally {
			if(schema.existsClass("DAOTestClassA")) schema.dropClass("DAOTestClassA");
			if(schema.existsClass("DAOTestClassB")) schema.dropClass("DAOTestClassB");
			if(schema.existsClass("DAOTestClassRoot")) schema.dropClass("DAOTestClassRoot");
		}
	}
	
	@Test
	public void testProperMethodListOrder() throws Exception {
		Class<?> type = IDAOTestClassA.class;
		
		List<Method> methods = DAO.listMethods(type);
		assertEquals("getName", methods.get(0).getName());
		assertEquals("setName", methods.get(1).getName());
	}
	
	@Test
	@Sudo
	public void testDescribeAllTypes() {
		OSchema schema = tester.getMetadata().getSchema();
		try {
			DAO.describe(OSchemaHelper.bind(tester.getDatabaseSession()), IDAOAllTypesTestClass.class);
			assertTrue(schema.existsClass("DAOAllTypesTestClass"));
			assertTrue(schema.existsClass("IDAODummyClass"));
			OClass oClass = schema.getClass("DAOAllTypesTestClass");
			OClass dummyClass = schema.getClass("IDAODummyClass");
			
			assertTrue(!oClass.isAbstract());
			
			assertProperty(oClass, "boolean", OType.BOOLEAN, 10, false);
			assertProperty(oClass, "booleanPrimitive", OType.BOOLEAN, 20, true);
			assertProperty(oClass, "booleanDeclared", OType.BOOLEAN, 30, true);
			
			assertProperty(oClass, "integer", OType.INTEGER, 40);
			assertProperty(oClass, "short", OType.SHORT, 50);
			assertProperty(oClass, "long", OType.LONG, 60);
			assertProperty(oClass, "float", OType.FLOAT, 70);
			assertProperty(oClass, "double", OType.DOUBLE, 80);
			assertProperty(oClass, "dateTime", OType.DATETIME, 90);
			assertProperty(oClass, "date", OType.DATE, 250);
			assertProperty(oClass, "string", OType.STRING, 100);
			assertProperty(oClass, "binary", OType.BINARY, 110); //
			assertProperty(oClass, "decimal", OType.DECIMAL, 270);
			assertProperty(oClass, "byte", OType.BYTE, 230);
			assertProperty(oClass, "custom", OType.CUSTOM, 260);
			assertProperty(oClass, "transient", OType.TRANSIENT, 240);
			assertProperty(oClass, "any", OType.ANY, 290);
			
			assertProperty(oClass, "link", OType.LINK, 190, dummyClass, null);
			assertProperty(oClass, "linkList", OType.LINKLIST, 200, dummyClass, null);
			assertProperty(oClass, "linkSet", OType.LINKSET, 210, dummyClass, null);
			assertProperty(oClass, "linkMap", OType.LINKMAP, 220, dummyClass, null);
			assertProperty(oClass, "linkBag", OType.LINKBAG, 280, dummyClass, null);
			
			assertProperty(oClass, "embedded", OType.EMBEDDED, 120, dummyClass, null);
			assertProperty(oClass, "embeddedList", OType.EMBEDDEDLIST, 130, dummyClass, null);
			assertProperty(oClass, "embeddedSet", OType.EMBEDDEDSET, 150, dummyClass, null);
			assertProperty(oClass, "embeddedMap", OType.EMBEDDEDMAP, 170, dummyClass, null);
			
			assertProperty(oClass, "embeddedStringSet", OType.EMBEDDEDSET, 160, OType.STRING);
			assertProperty(oClass, "embeddedStringList", OType.EMBEDDEDLIST, 140, OType.STRING);
			assertProperty(oClass, "embeddedStringMap", OType.EMBEDDEDMAP, 180, OType.STRING);

			assertProperty(oClass, "docs", OType.LINKLIST, 0, dummyClass, null);

		} finally {
			if(schema.existsClass("DAOAllTypesTestClass")) schema.dropClass("DAOAllTypesTestClass");
			if(schema.existsClass("IDAODummyClass")) schema.dropClass("IDAODummyClass");
		}
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, Integer order, OClass linkedClass, String inverse) {
		OProperty prop = assertProperty(oClass, property, oType, order);
		assertEquals(linkedClass, prop.getLinkedClass());
		assertEquals(inverse, CustomAttribute.PROP_INVERSE.getValue(prop));
		return prop;
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, Integer order, OType linkedType) {
		OProperty prop = assertProperty(oClass, property, oType, order);
		assertEquals(linkedType, prop.getLinkedType());
		return prop;
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, Integer order, boolean notNull) {
		OProperty prop = assertProperty(oClass, property, oType, order);
		assertEquals(notNull, prop.isNotNull());
		return prop;
	}
	
	private OProperty assertProperty(OClass oClass, String property, OType oType, Integer order) {
		OProperty prop = oClass.getProperty(property);
		assertNotNull("Property '"+property+"'was not found on OClass:"+oClass, prop);
		assertEquals(oType, prop.getType());
		assertEquals(order, CustomAttribute.ORDER.getValue(prop));
		return prop;
	}
	
	@Test
	@Sudo
	public void testInheritedClass() {
		OSchema schema = tester.getMetadata().getSchema();
		try {
			DAO.describe(OSchemaHelper.bind(tester.getDatabaseSession()), IDAOTestClassA.class);
			IDAOTestClassA obj = DAO.create(IDAOTestClassA.class);
			obj.setName("TestInheritedClass");
			DAO.save(obj);
			IDAOTestClassRoot obj2 = DAO.provide(IDAOTestClassRoot.class, DAO.asDocument(obj));
			assertTrue(obj2 instanceof IDAOTestClassA);
			assertEquals(obj.hashCode(), obj2.hashCode());
			assertTrue(obj2.equals(obj));
			
			IDAOTestClassB obj3 = DAO.create(IDAOTestClassB.class);
			DAO.save(obj3);
			assertFalse(obj.equals(obj3));
		} finally {
			if(schema.existsClass("DAOTestClassA")) schema.dropClass("DAOTestClassA");
			if(schema.existsClass("DAOTestClassB")) schema.dropClass("DAOTestClassB");
			if(schema.existsClass("DAOTestClassRoot")) schema.dropClass("DAOTestClassRoot");
		}
	}
}
