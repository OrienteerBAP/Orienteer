package org.orienteer.devutils;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;

import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import ru.ydn.wicket.wicketconsole.ScriptExecutor;
import ru.ydn.wicket.wicketconsole.ScriptExecutorHolder;


@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestModule
{
	@Inject
	private OrienteerTester tester;
    
	@Test
	public void testModuleLoaded()
	{
	    OrienteerWebApplication app = tester.getApplication();
	    assertNotNull(app);
	    IOrienteerModule module = app.getModuleByName("devutils");
	    assertNotNull(module);
	    assertTrue(module instanceof Module);
	}
	
	@Test
	public void testSimpleSQL() {
		ScriptExecutor se = ScriptExecutorHolder.get().getScriptExecutor();
		assertEquals(tester.getSchema().getClass("OUser").count(), 
							((OResultSet<ODocument>)se.executeWithoutHistory("SELECT count(1) from OUser","SQL",null).getResult()).get(0).field("count"));
	}
}
