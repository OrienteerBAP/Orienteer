package org.orienteer.core.resource;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

@RunWith(OrienteerTestRunner.class)
public class OContentShareResourceTest {

    private static final Logger LOG = LoggerFactory.getLogger(OContentShareResourceTest.class);

    @Inject
    private WicketTester tester;


    private ODocument doc;

    @Before
    public void init() {
        doc = DBClosure.sudo(db -> {
            OSchema schema = db.getMetadata().getSchema();
            OClass test = schema.createClass("Customer");
            test.createProperty("name", OType.STRING);
            test.createProperty("phone", OType.STRING);

            ODocument document = new ODocument("Customer");
            document.field("name", "Test Name");
            document.save();
            return document;
        });
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db -> db.getMetadata().getSchema().dropClass("Customer"));
    }

    @Test
    public void testRetrieveContent() {
        tester.executeUrl(OContentShareResource.urlFor(doc, "name", null, false).toString());
        MockHttpServletResponse response = tester.getLastResponse();
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    }

    @Test
    public void testRetrieveNotExistsContent() {
        tester.executeUrl(OContentShareResource.urlFor(doc, "notFound", "text/plain", false).toString());
        MockHttpServletResponse response = tester.getLastResponse();
        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    public void testRetrieveEmptyContent() {
        tester.executeUrl(OContentShareResource.urlFor(doc, "phone", "text/plain", false).toString());
        MockHttpServletResponse response = tester.getLastResponse();
        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_FOUND);
    }
    
    @Test
    public void testUrls() {
    	tester.setUseRequestUrlAsBase(true);
    	System.out.println("Local URL: "+OContentShareResource.urlFor(doc, "phone", "text/plain", false));
    	System.out.println("Full  URL: "+OContentShareResource.urlFor(doc, "phone", "text/plain", true));

    	tester.executeUrl("./api/echo/"+doc.getIdentity().toString().substring(1)+"/phone?full=false");
    	MockHttpServletResponse response = tester.getLastResponse();
    	String ret = response.getDocument();
    	assertEquals(OContentShareResource.urlFor(doc, "phone", "text/plain", false), ret);
    	
    	tester.executeUrl("./api/echo/"+doc.getIdentity().toString().substring(1)+"/phone?full=true");
    	response = tester.getLastResponse();
    	ret = response.getDocument();
    	assertEquals(OContentShareResource.urlFor(doc, "phone", "text/plain", true), ret);
    }
}
