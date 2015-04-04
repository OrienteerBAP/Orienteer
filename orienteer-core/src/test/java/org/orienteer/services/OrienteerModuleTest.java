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
package org.orienteer.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

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
	 * Test of configure method, of class OrienteerModule.
	 */
	@Test
	@Ignore
	public void testConfigure() {
		System.out.println("configure");
		OrienteerModule instance = new OrienteerModule();
		instance.configure();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of lookupFile method, of class OrienteerModule.
	 * @throws java.net.MalformedURLException
	 */
	@Test
	public void testRetrieveProperties() throws MalformedURLException, IOException {
		//guice property values need to be bound with values from
		//orienteer-test.properties
		String propertyFileNamePropertyName = "test.something";
		String propertyFileName = "some.filename";
		//system property not set
		String oldProp = System.clearProperty(OrienteerModule.PROPERTIES_RESOURCE_NAME_PROPERTY_NAME); //set again after the test -> don't run in parallel
			//without further precautions
		Properties expResult = OrienteerModule.PROPERTIES_DEFAULT;
		Properties result = OrienteerModule.retrieveProperties();
		assertEquals(expResult, result);
		//system property set
		File propertyFile = File.createTempFile("orienteer-test", null);
		System.setProperty(propertyFileNamePropertyName, propertyFile.getAbsolutePath());
		expResult = OrienteerModule.PROPERTIES_DEFAULT;
		result = OrienteerModule.retrieveProperties();
		assertEquals(expResult, result);
		//test creation of default properties file
		Properties propertyFileProperties = new Properties();
		propertyFileProperties.setProperty("a", "b");
		OutputStream propertyFileOutputStream = new FileOutputStream(propertyFile);
		propertyFileProperties.store(propertyFileOutputStream, "comment");
		propertyFileOutputStream.flush();
		propertyFileOutputStream.close();
		System.setProperty(OrienteerModule.PROPERTIES_FILE_NAME_PROPERTY_NAME, propertyFile.getAbsolutePath());
		expResult = new Properties();
		expResult.putAll(OrienteerModule.PROPERTIES_DEFAULT);
		expResult.setProperty("a", "b");
		result = OrienteerModule.retrieveProperties();
		assertEquals(expResult, result);
		
		System.setProperty(OrienteerModule.PROPERTIES_RESOURCE_NAME_PROPERTY_NAME, oldProp);
	}
	
}
