package org.orienteer.core;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.orienteer.core.method.AnnotatedMethod;
import org.orienteer.core.method.IMethod;
import org.orienteer.core.method.MethodManager;
import org.orienteer.core.method.SimpleMethod;
import org.orienteer.core.method.EmptyMethodContext;
import org.orienteer.junit.OrienteerTestRunner;
import static org.junit.Assert.*;

import java.util.List;

import com.google.inject.Singleton;



@RunWith(OrienteerTestRunner.class)
@Singleton
public class MethodsTest {
	
	@Test
	public void baseTest() throws Exception{
//		MethodManager.get().reload();
		List<IMethod> methods = MethodManager.get().getMethods(new EmptyMethodContext());
		assertTrue(methods.size()>0);
		int equals = 0;
		for (IMethod iMethod : methods) {
			if (iMethod instanceof AnnotatedMethod || iMethod instanceof SimpleMethod){
				equals++;
			}
		}
		assertEquals(1, equals);
		
	}

}
