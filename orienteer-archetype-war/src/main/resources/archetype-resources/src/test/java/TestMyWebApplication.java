package ${package};

import org.orienteer.junit.OrienteerTestRunner;

import org.orienteer.junit.OrienteerTester;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Singleton;


@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestMyWebApplication
{
	@Inject
	private OrienteerTester tester;
    
	@Test
	public void testWebApplicationClass()
	{
	    assertTrue(tester.getApplication() instanceof MyWebApplication);
	}
}
