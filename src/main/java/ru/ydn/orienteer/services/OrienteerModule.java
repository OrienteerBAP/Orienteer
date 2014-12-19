package ru.ydn.orienteer.services;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.wicket.protocol.http.WebApplication;

import ru.ydn.orienteer.OrienteerWebApplication;
import ru.ydn.orienteer.components.properties.UIVisualizersRegistry;
import ru.ydn.orienteer.services.impl.GuiceOrientDbSettings;
import ru.ydn.orienteer.services.impl.OClassIntrospector;
import ru.ydn.orienteer.services.impl.OrienteerWebjarsSettings;
import ru.ydn.orienteer.standalone.StartStandalone;
import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import de.agilecoders.wicket.webjars.settings.IWebjarsSettings;

public class OrienteerModule extends AbstractModule
{
	@Override
	protected void configure() {
		bind(WebApplication.class).to(OrienteerWebApplication.class);
		final Properties properties = new Properties();
		try
		{
			URL propertiesURL = StartStandalone.lookupPropertiesURL();
			if(propertiesURL==null) throw new ProvisionException("Properties files was not found");
			else properties.load(propertiesURL.openStream());
		} catch (FileNotFoundException e)
		{
			throw new ProvisionException("Properties files was not found", e);
		} catch (IOException e)
		{
			throw new ProvisionException("Properties files can't be read", e);
		}
		Names.bindProperties(binder(), properties);
		bind(Properties.class).annotatedWith(Orienteer.class).toInstance(properties);
		bind(IOrientDbSettings.class).to(GuiceOrientDbSettings.class);
		bind(IOClassIntrospector.class).to(OClassIntrospector.class);
		bind(UIVisualizersRegistry.class).asEagerSingleton();
		bind(IWebjarsSettings.class).to(OrienteerWebjarsSettings.class).asEagerSingleton();
	}
	
	@Provides
	public ODatabaseDocument getDatabaseRecord()
	{
		return DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
	}
	
	@Provides
	public OSchema getSchema(ODatabaseDocument db)
	{
		return db.getMetadata().getSchema();
	}

}
