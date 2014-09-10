package ru.ydn.orienteer.junit;

import org.junit.runners.model.InitializationError;

import ru.ydn.orienteer.services.OrienteerModule;
import ru.ydn.orienteer.standalone.StartStandalone;

import com.google.inject.util.Modules;

public class OrienteerTestRunner extends GuiceTestRunner
{
	static
	{
		System.out.println("Using embedded mode");
		System.setProperty(StartStandalone.PROPERTIES_FILE_NAME, StartStandalone.class.getResource("standalone.properties").toString());
	}
	
	public OrienteerTestRunner(Class<?> classToRun) throws InitializationError
	{
		super(classToRun, Modules.override(new OrienteerModule()).with(new TestOrieenteerModule()));
	}
}
