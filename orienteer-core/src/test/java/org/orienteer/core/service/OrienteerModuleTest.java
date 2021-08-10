package org.orienteer.core.service;

import org.junit.Test;
import org.orienteer.core.util.StartupPropertiesLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test modules loading
 * internal implementation notes:
 * - testLookupPropertiesURL relies on external resources and therefore would be
 * an integration test
*/
public class OrienteerModuleTest {
	
	public OrienteerModuleTest() {
	}

	/**
	 * Test of lookupFile method, of class OrienteerModule.
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	@Test
	public void testRetrieveProperties() throws MalformedURLException, IOException {
		//clear system property
		String oldProp = System.clearProperty(StartupPropertiesLoader.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME);
		try {
			System.setProperty(StartupPropertiesLoader.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "non-existing-qualifier");
			Properties result = StartupPropertiesLoader.retrieveProperties();
			assertNotNull(result);
			//system property set
			File propertyFile = File.createTempFile("orienteer-test-temp", ".properties");
			
			{
				FileWriter writer = new FileWriter(propertyFile);
				writer.write("myproperty=myvalue");
				writer.flush(); 
				writer.close();
			}
			System.setProperty("orienteer-test-temp.properties", propertyFile.getAbsolutePath());
			System.setProperty(StartupPropertiesLoader.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "orienteer-test-temp");
			result = StartupPropertiesLoader.retrieveProperties();
			assertTrue(result.containsKey("myproperty"));
			assertEquals("myvalue", result.getProperty("myproperty"));
			result.remove("myproperty");
			Properties orienteerProperties = StartupPropertiesLoader.PROPERTIES_DEFAULT;
			// putting all system properties to orienteer
			orienteerProperties.putAll(System.getProperties());
			//Root password was deleted due to security reason by OrientDB - we should do the same
			orienteerProperties.remove("orientdb.root.password");
			result.remove("orientdb.root.password");
			assertEquals(orienteerProperties, result);
			//loading from resources
			System.setProperty(StartupPropertiesLoader.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "test-custom-startup-properties");
			result = StartupPropertiesLoader.retrieveProperties();
			assertTrue(result.containsKey("customkey"));
			assertEquals("customvalue", result.getProperty("customkey"));
			result.remove("customkey");
			orienteerProperties.putAll(System.getProperties());
			assertEquals(orienteerProperties, result);
		} finally {
			System.out.println("SETTING BACK OLD QUOLIFIER:"+oldProp);
			if(oldProp!=null) System.setProperty(StartupPropertiesLoader.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, oldProp);
		}
		
	}
	
}
