/*
 * Copyright 2015 richter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orienteer.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.orienteer.core.service.OrienteerModule;

/**
 *
 * @author richter
 */
/*
internal implementation notes:
- testLookupPropertiesURL relies on external resources and therefore would be
an integration test
*/
public class OrienteerModuleTest {
	
	public OrienteerModuleTest() {
	}

	/**
	 * Test of lookupFile method, of class OrienteerModule.
	 * @throws java.net.MalformedURLException
	 */
	@Test
	public void testRetrieveProperties() throws MalformedURLException, IOException {
		//clear system property
		String oldProp = System.clearProperty(OrienteerModule.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME);
		try {
			System.setProperty(OrienteerModule.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "non-existing-qualifier");
			Properties result = OrienteerModule.retrieveProperties();
			assertEquals(OrienteerModule.PROPERTIES_DEFAULT, result);
			//system property set
			File propertyFile = File.createTempFile("orienteer-test-temp", ".properties");
			
			{
				FileWriter writer = new FileWriter(propertyFile);
				writer.write("myproperty=myvalue");
				writer.flush(); 
				writer.close();
			}
			System.setProperty("orienteer-test-temp.properties", propertyFile.getAbsolutePath());
			System.setProperty(OrienteerModule.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "orienteer-test-temp");
			result = OrienteerModule.retrieveProperties();
			assertTrue(result.containsKey("myproperty"));
			assertEquals("myvalue", result.getProperty("myproperty"));
			result.remove("myproperty");
			assertEquals(OrienteerModule.PROPERTIES_DEFAULT, result);
			//loading from resources
			System.setProperty(OrienteerModule.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, "test-custom-startup-properties");
			result = OrienteerModule.retrieveProperties();
			assertTrue(result.containsKey("customkey"));
			assertEquals("customvalue", result.getProperty("customkey"));
			result.remove("customkey");
			assertEquals(OrienteerModule.PROPERTIES_DEFAULT, result);
		} finally {
			System.out.println("SETTING BACK OLD QUOLIFIER:"+oldProp);
			if(oldProp!=null) System.setProperty(OrienteerModule.ORIENTEER_PROPERTIES_QUALIFIER_PROPERTY_NAME, oldProp);
		}
		
	}
	
}
