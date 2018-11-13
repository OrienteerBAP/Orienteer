package org.orienteer.users;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.web.HomePage;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RestorePasswordResource;
import org.orienteer.users.service.IOrienteerUsersService;
import org.orienteer.users.web.DefaultRestorePasswordPage;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class RestorePasswordComponentTest {

    @Inject
    private IOrienteerUsersService usersService;

    @Inject
    private OrienteerTester tester;

    private OrienteerUser testUser;

    @Before
    public void init() {
        testUser = usersService.createUser();
        testUser.setFirstName("FirstName")
                .setLastName("LastName")
                .setEmail(UUID.randomUUID() + "@gmail.com");

        testUser.setName(testUser.getEmail())
                .setPassword(UUID.randomUUID().toString())
                .setAccountStatus(OSecurityUser.STATUSES.ACTIVE);

        DBClosure.sudoSave(testUser);

        usersService.restoreUserPassword(testUser);
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db -> {
            db.command(new OCommandSQL("delete from ?")).execute(testUser.getDocument());
        });
        tester.signOut();
    }

    @Test
    public void testRestorePassword() {
        final String password = UUID.randomUUID().toString();
        openRestorePage();
        fillAndSubmitRestorePasswordPanel(password);
        login(password);
    }

    private void openRestorePage() {
        PageParameters params = new PageParameters();
        params.add(RestorePasswordResource.PARAMETER_ID, testUser.getRestoreId());
        tester.startPage(DefaultRestorePasswordPage.class, params);

        tester.assertRenderedPage(DefaultRestorePasswordPage.class);
        tester.assertVisible("container:restorePanel");
        tester.assertInvisible("container:restoreSuccessMessage");
    }

    private void fillAndSubmitRestorePasswordPanel(String password) {
        FormTester formTester = tester.newFormTester("container:restorePanel:form");
        formTester.setValue("password", password);
        formTester.setValue("confirmPassword", password);

        tester.clickLink("container:restorePanel:form:restoreButton:command", true);

        tester.assertVisible("container:restoreSuccessMessage");
        tester.assertInvisible("container:restorePanel");
    }

    private void login(String password) {
        tester.startPage(HomePage.class);
        FormTester formTester = tester.newFormTester("container:loginPanel:form");
        formTester.setValue("username", testUser.getName());
        formTester.setValue("password", password);

        tester.clickLink("container:loginPanel:form:loginButtonsPanel:loginButton:command", true);

        OrienteerWebSession session = (OrienteerWebSession) tester.getSession();
        assertNotNull(session.getUser());
        assertEquals(testUser.getName(), session.getUser().getName());
    }
}
