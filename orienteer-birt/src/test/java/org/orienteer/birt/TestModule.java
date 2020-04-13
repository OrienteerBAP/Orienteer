package org.orienteer.birt;

import org.orienteer.birt.component.BirtHtmlReportPanel;
import org.orienteer.birt.component.resources.HtmlBirtResource;
import org.orienteer.birt.component.service.BirtReportFileConfig;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.module.IOrienteerModule;

import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.wicket.mock.MockWebRequest;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.response.ByteArrayResponse;
import org.apache.wicket.util.io.IOUtils;
import org.eclipse.birt.report.engine.api.EngineException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class TestModule
{
	@Inject
	private OrienteerTester tester;
    
	@Test
	public void testModuleLoaded() throws EngineException, IOException
	{
	    OrienteerWebApplication app = tester.getApplication();
	    assertNotNull(app);
	    IOrienteerModule module = app.getModuleByName(Module.MODULE_NAME);
	    assertNotNull(module);
	    assertTrue(module instanceof Module);
	    ClassLoader loader = getClass().getClassLoader();
	    URL innerResource = loader.getResource("test.rptdesign");
	    
	    HtmlBirtResource resource = new HtmlBirtResource(
	    		new BirtHtmlReportPanel("rp",
	    				new BirtReportFileConfig(innerResource.getFile())
	    		)
	    );
	    MockWebRequest request = new MockWebRequest(new Url());
	    ByteArrayResponse response = new ByteArrayResponse();
	    Attributes attributes = new IResource.Attributes(request, response); 
	    
	    resource.respond(attributes);
	    
	    String newResult = new String(response.getBytes());
	    FileInputStream savedStream = new FileInputStream(loader.getResource("test.rptdesign.result.html").getFile());
	    String oldResult;
	    
	    try {
	        oldResult = IOUtils.toString(savedStream);
	    } finally {
	    	savedStream.close();
	    }
	    
	    assertEquals(oldResult.replace("\r\n",  "\n"),newResult.replace("\r\n",  "\n"));
	    //ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	    //OutputStream stream = response.getOutputStream();
	    
	    //FileOutputStream out = new FileOutputStream("test.rptdesign.result.html");
	    //out.write(response.getBytes());
	    //out.close();

	    //System.out.println(new String(response.getBytes()));
	    
	    //BirtHtmlReportPanel testedPanel = new BirtHtmlReportPanel("panel", null);
	    
	    
	}
}



















