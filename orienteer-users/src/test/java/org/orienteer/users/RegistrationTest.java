package org.orienteer.users;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.web.HomePage;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.users.component.DefaultRegistrationPanel;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RegistrationResource;
import org.orienteer.users.service.IOrienteerUsersService;
import org.orienteer.users.util.OUsersDbUtils;
import org.orienteer.users.web.DefaultRegistrationPage;
import ru.ydn.wicket.wicketorientdb.model.ODocumentWrapperModel;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrienteerTestRunner.class)
public class RegistrationTest {

    @Inject
    private WicketTester tester;

    @Inject
    private IOrienteerUsersService usersService;

    private OrienteerUser testUser;
    
    @Before
    public void init() {
        testUser = usersService.createUser();
        testUser.setFirstName("FirstName")
                .setLastName("LastName")
                .setEmail(UUID.randomUUID() + "@gmail.com");

        testUser.setName(testUser.getEmail())
                .setPassword("1234567890")
                .setAccountStatus(OSecurityUser.STATUSES.SUSPENDED);
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db -> {
            String sql = String.format("delete from %s where %s = ?", OUser.CLASS_NAME, OrienteerUser.PROP_EMAIL);
            db.command(new OCommandSQL(sql)).execute(testUser.getEmail());
        });
    }
    
    @Test
    public void testRenderRegistrationPage() {
        tester.startPage(DefaultRegistrationPage.class);
        tester.assertRenderedPage(DefaultRegistrationPage.class);
        tester.assertVisible("container:registrationPanel");
        tester.assertInvisible("container:registrationSuccessLabel");
    }

    @Test
    public void testRegistrationPanel() {
        DefaultRegistrationPanel panel = new DefaultRegistrationPanel("panel", new ODocumentWrapperModel<>(usersService.createUser()));
        tester.startComponentInPage(panel);

        FormTester formTester = tester.newFormTester("panel:form", false);
        formTester.setValue("firstName", testUser.getFirstName());
        formTester.setValue("lastName", testUser.getLastName());
        formTester.setValue("email", testUser.getEmail());
        formTester.setValue("password", testUser.getPassword());
        formTester.setValue("confirmPassword", testUser.getPassword());

        tester.clickLink("panel:form:registerButton:command", true);

        OrienteerUser user = panel.getModelObject();
        assertEquals(testUser.getFirstName(), user.getFirstName());
        assertEquals(testUser.getLastName(), user.getLastName());
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.getName(), user.getName());
        assertEquals(testUser.getPassword(), user.getPassword());
        assertEquals(testUser.getAccountStatus(), user.getAccountStatus());
    }

    @Test
    public void testRegistrationUser() {
        OrienteerUser user = registerUser();
        openRegistrationLink(user);
        DBClosure.sudoConsumer(db -> user.load());
        login(user);
    }

    private OrienteerUser registerUser() {
        tester.startPage(DefaultRegistrationPage.class);

        FormTester formTester = tester.newFormTester("container:registrationPanel:form", false);
        formTester.setValue("firstName", testUser.getFirstName());
        formTester.setValue("lastName", testUser.getLastName());
        formTester.setValue("email", testUser.getEmail());
        formTester.setValue("password", testUser.getPassword());
        formTester.setValue("confirmPassword", testUser.getPassword());

        tester.clickLink("container:registrationPanel:form:registerButton:command", true);

        tester.assertInvisible("container:registrationPanel");
        tester.assertVisible("container:registrationSuccessLabel");

        return OUsersDbUtils.getUserByEmail(testUser.getEmail())
                .orElseThrow(IllegalStateException::new);
    }

    private void openRegistrationLink(OrienteerUser user) {
        PageParameters params = new PageParameters();
        params.add(RegistrationResource.PARAMETER_ID, user.getId());
        tester.startResourceReference(getRegistrationResourceReference(), params);
    }

    private void login(OrienteerUser user) {
        tester.startPage(HomePage.class);
        FormTester formTester = tester.newFormTester("signInPanel:signInForm");
        formTester.setValue("username", user.getName());
        formTester.setValue("password", testUser.getPassword());
        formTester.submit();
        OrienteerWebSession session = (OrienteerWebSession) tester.getSession();
        assertNotNull(session.getUser());
        assertEquals(user.getName(), session.getUser().getName());
    }

    private ResourceReference getRegistrationResourceReference() {
        return tester.getApplication().getSharedResources().get(RegistrationResource.RES_KEY);
    }
}
