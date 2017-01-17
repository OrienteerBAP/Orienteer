package org.orienteer.core;

import static org.junit.Assert.*;

import java.util.Collection;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.component.property.DisplayMode;
import org.orienteer.core.service.CustomTestModule;
import org.orienteer.core.web.BrowseOClassPage;
import org.orienteer.core.web.LoginPage;
import org.orienteer.core.web.ODocumentPage;
import org.orienteer.core.web.schema.OClassPage;
import org.orienteer.core.web.schema.OIndexPage;
import org.orienteer.core.web.schema.OPropertyPage;
import org.orienteer.core.web.schema.SchemaPage;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.testenv.TestEnvOrienteerWebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class OrienteerMainTest
{
	private static final Logger LOG = LoggerFactory.getLogger(OrienteerMainTest.class);
	@Inject
	private WicketTester tester;

	public OrienteerMainTest() {
	}
	
	@Test
	public void testWicketTester()
	{
		assertTrue(tester instanceof OrienteerTester);
	}
	
	@Before
	public void performLogin()
	{
		tester.startPage(SchemaPage.class);
		if(tester.getLastRenderedPage() instanceof LoginPage)
		{
			FormTester formTester = tester.newFormTester("signInPanel:signInForm");
			IOrientDbSettings settings = ((OrienteerWebApplication)tester.getApplication()).getOrientDbSettings();
            formTester.setValue("username", settings.getAdminUserName());
            formTester.setValue("password", settings.getAdminPassword());
            formTester.submit();
		}
		tester.assertRenderedPage(SchemaPage.class);
	}
	
	@Test
	public void testWebApplicationRedefenition()
	{
		assertTrue(tester.getApplication() instanceof TestEnvOrienteerWebApplication);
	}
	
	@Test
	public void testLoadingCustomGuiceModule() {
		assertEquals(CustomTestModule.RANDOM_STRING, 
				((OrienteerWebApplication)tester.getApplication())
						.getServiceInstance(CustomTestModule.ITestInterface.class).getKey());
	}
	
	@Test
	public void testMainPages() throws Exception
	{
		tester.startPage(SchemaPage.class);
		tester.assertRenderedPage(SchemaPage.class);
	}
	
	@Test
	public void testBrowsePages() throws Exception
	{
		ODatabaseDocument db = getDatabase();
		Collection<OClass> classes = db.getMetadata().getSchema().getClasses();
		PageParameters parameters = new PageParameters();
		for (OClass oClass : classes)
		{
			parameters.set("className", oClass.getName());
			LOG.info("Rendering browse page for class '"+oClass.getName()+"'");
			tester.startPage(BrowseOClassPage.class, parameters);
			tester.assertRenderedPage(BrowseOClassPage.class);
		}
	}
	
	@Test
	public void testShowDummyDocuments() throws Exception
	{
		ODatabaseDocument db = getDatabase();
		Collection<OClass> classes = db.getMetadata().getSchema().getClasses();
		for (OClass oClass : classes)
		{
			ODocument doc = new ODocument(oClass);
			LOG.info("Rendering VIEW document page for class '"+oClass.getName()+"'");
			tester.startPage(new ODocumentPage(doc));
			tester.assertRenderedPage(ODocumentPage.class);
			LOG.info("Rendering EDIT document page for class '"+oClass.getName()+"'");
			tester.startPage(new ODocumentPage(doc).setModeObject(DisplayMode.EDIT));
			tester.assertRenderedPage(ODocumentPage.class);
		}
	}
	
	@Test
	public void testViewClassesAndPropertiesPages() throws Exception
	{
		ODatabaseDocument db = getDatabase();
		Collection<OClass> classes = db.getMetadata().getSchema().getClasses();
		PageParameters parameters = new PageParameters();
		for (OClass oClass : classes)
		{
			parameters.clearNamed();
			parameters.set("className", oClass.getName());
			LOG.info("Rendering page for class '"+oClass.getName()+"'");
			tester.startPage(OClassPage.class, parameters);
			tester.assertRenderedPage(OClassPage.class);
			Collection<OProperty> properties = oClass.properties();
			for (OProperty oProperty : properties)
			{
				parameters.set("propertyName", oProperty.getName());
				LOG.info("Rendering page for property '"+oProperty.getFullName()+"'");
				tester.startPage(OPropertyPage.class, parameters);
				tester.assertRenderedPage(OPropertyPage.class);
			}
			Collection<OIndex<?>> indexes = oClass.getIndexes();
			for (OIndex<?> oIndex : indexes)
			{
				parameters.set("indexName", oIndex.getName());
				LOG.info("Rendering page for index '"+oIndex.getName()+"'");
				tester.startPage(OIndexPage.class, parameters);
				tester.assertRenderedPage(OIndexPage.class);
			}
		}
	}
	
	private ODatabaseDocument getDatabase()
	{
		return ((OrientDbWebSession)tester.getSession()).getDatabase();
	}
}
