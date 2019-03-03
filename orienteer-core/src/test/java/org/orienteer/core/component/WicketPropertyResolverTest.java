package org.orienteer.core.component;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class WicketPropertyResolverTest {

	@Inject
	private OrienteerTester tester;
	
	@Before
	public void mountPage() {
		tester.getApplication().mountPage("/wicketProperty", WicketPropertyResolverPage.class);
	}
	
	@After
	public void unmountPage() {
		tester.getApplication().unmount("/wicketProperty");
	}
	
	@Test
	public void testPageRendered() {
		tester.startPage(WicketPropertyResolverPage.class);
		tester.assertRenderedPage(WicketPropertyResolverPage.class);
		tester.assertContains("reader");
		tester.assertContains("ACTIVE");
		tester.assertContains("Default");
		System.out.println(tester.getLastResponseAsString());
	}
}
