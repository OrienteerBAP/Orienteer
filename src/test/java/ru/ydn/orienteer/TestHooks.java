package ru.ydn.orienteer;

import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import ru.ydn.orienteer.junit.OrienteerTestRunner;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestHooks
{
	@Test
	public void testCalculableHook() throws Exception
	{
		assertTrue(OrientDbWebSession.get().signIn("admin", "admin"));
		ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		
		assertFalse(db.isClosed());
		db.commit();
		if(schema.existsClass("TestClassA")) schema.dropClass("TestClassA");
		OClass oClass = schema.createClass("TestClassA");
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
		} finally
		{
			schema.dropClass(oClass.getName());
			OrientDbWebSession.get().signOut();
		}
	}
}
