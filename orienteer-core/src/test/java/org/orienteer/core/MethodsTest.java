package org.orienteer.core;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.MethodEmptyData;
import org.orienteer.core.method.MethodManager;
import org.orienteer.core.method.SimpleMethod;
import org.orienteer.core.module.UserOnlineModule;
import org.orienteer.junit.OrienteerTestRunner;
import static org.junit.Assert.*;

import java.util.List;

import com.google.inject.Singleton;



@RunWith(OrienteerTestRunner.class)
@Singleton
public class MethodsTest {
	
	@Test
	public void baseTest() throws Exception{
		/*
		MethodManager.get().addModule(UserOnlineModule.class);
		MethodManager.get().reload();
		List<IMethod> methods = MethodManager.get().getMethods(new MethodEmptyData());
		assertEquals(1,methods.size());
		assertTrue(methods.get(0) instanceof SimpleMethod);
		*/
	}

}
