package ru.ydn.orienteer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import ru.ydn.orienteer.junit.OrienteerTestRunner;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestHooks
{
	private static final String TEST_CLASS_A = "TestClassA";
	private static final String TEST_CLASS_B = "TestClassB";
	private static final String TEST_CLASS_C = "TestClassC";
	@Test
	public void testCalculableHook() throws Exception
	{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		
		assertFalse(db.isClosed());
		db.commit();
		if(schema.existsClass(TEST_CLASS_A)) schema.dropClass(TEST_CLASS_A);
		OClass oClass = schema.createClass(TEST_CLASS_A);
		try
		{
			oClass.createProperty("a", OType.INTEGER);
			oClass.createProperty("b", OType.INTEGER);
			OProperty cProperty = oClass.createProperty("c", OType.INTEGER);
			CustomAttributes.CALCULABLE.setValue(cProperty, true);
			CustomAttributes.CALC_SCRIPT.setValue(cProperty, "select sum(a, b) as value from TestClassA where @rid = ?");
			
			ODocument doc = new ODocument(oClass);
			doc.field("a", 2);
			doc.field("b", 2);
			doc.save();
			doc.reload();
			assertEquals(4, doc.field("c"));
			doc.field("a", 3);
			doc.field("b", 3);
			doc.save();
			doc.reload();
			assertEquals(6, doc.field("c"));
			db.begin();
			doc.field("a", 4);
			doc.field("b", 4);
			doc.save();
			doc.reload();
			assertEquals(8, doc.field("c"));
			db.commit();
			db.begin();
			doc.field("a", 5);
			doc.field("b", 5);
			doc.save();
			doc.reload();
			assertEquals(10, doc.field("c"));
			db.commit();
		} finally
		{
			if(db.getTransaction().isActive()) db.commit();
			schema.dropClass(TEST_CLASS_A);
			OrientDbWebSession.get().signOut();
		}
	}
	
	@Test
	public void testReferencesHook() throws Exception
	{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		
		assertFalse(db.isClosed());
		db.commit();
		if(schema.existsClass(TEST_CLASS_B)) schema.dropClass(TEST_CLASS_B);
		OClass classB = schema.createClass(TEST_CLASS_B);
		try
		{
			OProperty parent = classB.createProperty("parent", OType.LINK);
			OProperty child = classB.createProperty("child", OType.LINKLIST);
			CustomAttributes.PROP_INVERSE.setValue(parent, child);
			CustomAttributes.PROP_INVERSE.setValue(child, parent);
			//Create root object
			ODocument rootDoc = new ODocument(classB);
			rootDoc.save();
			//Create first child
			ODocument child1Doc = new ODocument(classB);
			child1Doc.field("parent", rootDoc);
			child1Doc.save();
			//Check that back ref is here
			rootDoc.reload();
			Collection<OIdentifiable> childCollection = rootDoc.field("child");
			assertEquals(1, childCollection.size());
			assertTrue(childCollection.contains(child1Doc));
			//Create second child
			ODocument child2Doc = new ODocument(classB);
			child2Doc.field("parent", rootDoc);
			child2Doc.save();
			//Check that back ref to 2 child doc here
			rootDoc.reload();
			childCollection = rootDoc.field("child");
			assertEquals(2, childCollection.size());
			assertTrue(childCollection.contains(child1Doc));
			assertTrue(childCollection.contains(child2Doc));
			//Remove first child;
			child1Doc.reload();
			child1Doc.delete();
			//Check that back ref to second child doc here
			rootDoc.reload();
			childCollection = rootDoc.field("child");
			assertEquals(1, childCollection.size());
			assertTrue(childCollection.contains(child2Doc));
			
			//Create 3rd child
			ODocument child3Doc = new ODocument(classB);
			child3Doc.save();
			//Associate 3rd child with root by attribute
			childCollection.add(child3Doc);
			rootDoc.field("child", childCollection);
			rootDoc.save();
			//Check that association is correct for root
			rootDoc.reload();
			childCollection = rootDoc.field("child");
			assertEquals(2, childCollection.size());
			assertTrue(childCollection.contains(child2Doc));
			assertTrue(childCollection.contains(child3Doc));
			//Check that association is correct for child3
			child3Doc.reload();
			assertNotNull("Parent should be set", child3Doc.field("parent"));
			OIdentifiable rootId = child3Doc.field("parent");
			assertEquals(rootDoc, rootId.getRecord());
			
			//Now lets update parent for child2 to null
			child2Doc.field("parent", (Object)null);
			child2Doc.save();
			//Check root
			rootDoc.reload();
			childCollection = rootDoc.field("child");
			assertEquals(1, childCollection.size());
			assertTrue(childCollection.contains(child3Doc));
			
			//Lets delete reference to child3 by clear
			childCollection.clear();
//			childCollection.remove(child3Doc);
			rootDoc.field("child", childCollection);
			rootDoc.save();
			//Check back ref from Child3
			child3Doc.reload();
			assertNull(child3Doc.field("parent"));
			
			//Lets create one more Root to test setting collection from scratch
			ODocument root2Doc = new ODocument(classB);
			root2Doc.save();
			
			ODocument childTestForNull = new ODocument(classB);
			childTestForNull.save();
			assertNull(root2Doc.field("child"));
			root2Doc.field("child", Arrays.asList(childTestForNull));
			root2Doc.save();
			childTestForNull.reload();
			childCollection = root2Doc.field("child");
			assertEquals(1, childCollection.size());
			assertTrue(childCollection.contains(childTestForNull));
			assertEquals(root2Doc, childTestForNull.field("parent"));
		} finally
		{
			schema.dropClass(TEST_CLASS_B);
			OrientDbWebSession.get().signOut();
		}
	}
	
	@Test
	public void testReferencesHookDeepCase() throws Exception
	{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		
		assertFalse(db.isClosed());
		db.commit();
		if(schema.existsClass(TEST_CLASS_C)) schema.dropClass(TEST_CLASS_C);
		OClass classC = schema.createClass(TEST_CLASS_C);
		try
		{
			OProperty parent = classC.createProperty("parent", OType.LINK);
			OProperty child = classC.createProperty("child", OType.LINKLIST);
			CustomAttributes.PROP_INVERSE.setValue(parent, child);
			CustomAttributes.PROP_INVERSE.setValue(child, parent);
			
			ODocument doc1 = new ODocument(classC).save();
			ODocument doc2 = new ODocument(classC).save();
			ODocument doc3 = new ODocument(classC).save();
			
			doc1.field("child", Arrays.asList(doc2));
			doc1.save();
			doc2.reload();
			assertEquals(doc1, doc2.field("parent"));
			
			doc3.field("child", Arrays.asList(doc2));
			doc3.save();
			doc2.reload();
			assertEquals(doc3, doc2.field("parent"));
			doc1.reload();
			List<ODocument> childs = doc1.field("child");
			assertNotNull(childs);
			assertArrayEquals(new ODocument[0], childs.toArray(new ODocument[0]));
			
		} finally
		{
			schema.dropClass(TEST_CLASS_C);
			OrientDbWebSession.get().signOut();
		}
	}
}
