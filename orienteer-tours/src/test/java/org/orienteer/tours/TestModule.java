package org.orienteer.tours;

import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;

import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

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
	    IOrienteerModule module = app.getModuleByName("tours");
	    assertNotNull(module);
	    assertTrue(module instanceof OToursModule);
	}
}
