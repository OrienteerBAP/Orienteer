package org.orienteer.logger.server;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.logger.server.OLoggerModule;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Singleton;


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
	    IOrienteerModule module = app.getModuleByName(OLoggerModule.MODULE_OLOGGER_NAME);
	    assertNotNull(module);
	    assertTrue(module instanceof OLoggerModule);
	    //TODO : to add tests for incident logger
	}
}
