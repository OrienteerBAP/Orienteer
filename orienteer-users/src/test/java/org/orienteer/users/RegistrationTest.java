package org.orienteer.users;

import com.google.inject.Inject;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.users.resource.RegistrationResource;
import org.orienteer.users.web.DefaultRegistrationPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RunWith(OrienteerTestRunner.class)
public class RegistrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationTest.class);

    @Inject
    private WicketTester tester;

    @Test
    public void testRenderRegistrationPage() {
        tester.startPage(DefaultRegistrationPage.class);
        tester.assertRenderedPage(DefaultRegistrationPage.class);
        tester.assertVisible("container:registrationPanel");
        tester.assertInvisible("container:registrationSuccessLabel");
    }

    @Test
    public void testRenderRegistrationPanel() {

    }

    @Test
    public void testRegistrationResourceNotFoundUser() {
        ResourceReference registrationResource = tester.getApplication().getSharedResources().get(RegistrationResource.RES_KEY);
        PageParameters params = new PageParameters();
        params.add(RegistrationResource.PARAMETER_ID, UUID.randomUUID().toString());

        tester.startResourceReference(registrationResource, params);
        MockHttpServletResponse lastResponse = tester.getLastResponse();
        LOG.info("last response: {}", lastResponse.getRedirectLocation());
    }
}
