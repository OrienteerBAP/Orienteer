package org.orienteer.junit.tests;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.junit.Sudo;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.junit.Assert.*;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestOrineteerTestRunner extends AbstractTestInjection
{
	@Test
	@Sudo
	public void testSudo()
	{
		assertEquals("admin", tester3.getSession().getUsername());
	}
	
	@Test
	@Sudo(value="reader", password="reader")
	public void testSudoReader()
	{
		assertEquals("reader", tester3.getSession().getUsername());
	}
	
}
