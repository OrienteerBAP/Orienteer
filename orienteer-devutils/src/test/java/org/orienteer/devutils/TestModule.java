package org.orienteer.devutils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.core.util.OSchemaHelper;
import org.orienteer.devutils.web.ToolsPage;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;
import ru.ydn.wicket.wicketconsole.ScriptExecutor;
import ru.ydn.wicket.wicketconsole.ScriptExecutorHolder;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import static org.junit.Assert.*;


@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestModule
{
	@Inject
	private OrienteerTester tester;
	
	private static boolean isDevUtilsActivated = false;
	
	@Before
	public void activateModel() {
		if(!isDevUtilsActivated) OSchemaHelper.bind(tester.getDatabaseSession()).oClass(IOrienteerModule.OMODULE_CLASS)
					.oDocument(IOrienteerModule.OMODULE_NAME, "devutils")
					.doOnODocument(doc -> {
						doc.field(IOrienteerModule.OMODULE_ACTIVATE, true);
						DBClosure.sudoSave(doc);
						isDevUtilsActivated=true;
					});
	}
    
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

		OResultSet result = (OResultSet) se.executeWithoutHistory("SELECT count(1) as count from OUser", "SQL", null).getResult();

		assertEquals(tester.getSchema().getClass("OUser").count(), (long) result.next().getProperty("count"));
	}
	
	@Test
	@Sudo
	public void testPageLoad() {
		assertTrue(tester.getSession().isSignedIn());
		tester.startPage(ToolsPage.class, new PageParameters().add("tab", "console"));
		tester.assertRenderedPage(ToolsPage.class);
		tester.startPage(ToolsPage.class, new PageParameters().add("tab", "monitoring"));
		tester.assertRenderedPage(ToolsPage.class);
	}
}
