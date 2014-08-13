package ru.ydn.orienteer.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.wicket.protocol.http.WebApplication;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.properties.UIComponentsRegistry;
import ru.ydn.orienteer.services.impl.GuiceOrientDbSettings;
import ru.ydn.orienteer.services.impl.OClassIntrospector;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class OrienteerModule extends AbstractModule
{
	private static final String PROPERTIES_FILE_NAME = "orienteer.properties";
	@Override
	protected void configure() {
		bind(WebApplication.class).to(OrienteerWebApplication.class);
		final Properties properties = new Properties();
		File propertiesFile = lookupPropertiesFile();
		if(propertiesFile==null) throw new ProvisionException("Properties files was not found");
		try
		{
			properties.load(new FileReader(propertiesFile));
		} catch (FileNotFoundException e)
		{
			throw new ProvisionException("Properties files was not found");
		} catch (IOException e)
		{
			throw new ProvisionException("Properties files can't be read", e);
		}
		Names.bindProperties(binder(), properties);
		bind(Properties.class).annotatedWith(Orienteer.class).toInstance(properties);
		bind(IOrientDbSettings.class).to(GuiceOrientDbSettings.class);
		bind(IOClassIntrospector.class).to(OClassIntrospector.class);
		bind(UIComponentsRegistry.class).asEagerSingleton();
	}
	
	private File lookupPropertiesFile()
	{
		File file = new File(PROPERTIES_FILE_NAME);
		File dir = new File("").getAbsoluteFile();
		while(!file.exists() && dir!=null)
		{
			dir = dir.getParentFile();
			file = new File(dir, PROPERTIES_FILE_NAME);
		}
		return file;
	}
	
	@Provides
	public ODatabaseRecord getDatabaseRecord()
	{
		return ODatabaseRecordThreadLocal.INSTANCE.get();
	}

}
