package org.orienteer.devutils;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.devutils.web.ToolsPage;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;

import static org.junit.Assert.*;

import org.apache.wicket.request.mapper.parameter.PageParameters;
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
				(Object)  ((OResultSet<ODocument>)se.executeWithoutHistory("SELECT count(1) from OUser","SQL",null).getResult()).get(0).field("count"));
	}
	
	@Test
	@Sudo
	public void testPageLoad() {
		tester.startPage(ToolsPage.class, new PageParameters().add("tab", "console"));
		tester.assertRenderedPage(ToolsPage.class);
		tester.startPage(ToolsPage.class, new PageParameters().add("tab", "monitoring"));
		tester.assertRenderedPage(ToolsPage.class);
	}
}
