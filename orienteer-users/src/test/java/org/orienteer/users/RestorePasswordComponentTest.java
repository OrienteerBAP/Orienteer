package org.orienteer.users;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RestorePasswordResource;
import org.orienteer.users.service.IOrienteerUsersService;
import org.orienteer.users.web.DefaultRestorePasswordPage;
import org.orienteer.users.web.OUsersLoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrienteerTestRunner.class)
@Singleton
public class RestorePasswordComponentTest {

    private static final Logger LOG = LoggerFactory.getLogger(RestorePasswordComponentTest.class);

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
            db.command("delete from ?", testUser.getDocument());
        });
        tester.signOut();
    }

    @Test
    public void testRestore() {
//        DBClosure.sudoConsumer(db -> {
//            OSecurity security = db.getMetadata().getSecurity();
//
//            db.query("select from " + PerspectivesModule.OPerspective.CLASS_NAME)
//                    .stream()
//                    .map(res -> res.getElement().get())
//                    .forEach(element -> {
//                        Set<OElement> read = element.getProperty("_allowRead");
//
//                        LOG.info("[ADMIN] record: {} allowRead: {}", element, read.iterator().next());
//                    });
//
//
//            db.query("select from " + ORole.CLASS_NAME)
//                    .stream()
//                    .map(res -> res.getElement().get())
//                    .forEach(element -> {
//                        Set<OElement> read = element.getProperty("_allowRead");
//                        if (read != null && !read.isEmpty()) {
//                            LOG.info("[ADMIN] role: {} allowRead: {}", element, read.iterator().next());
//                        } else {
//                            LOG.info("[ADMIN] role: {}", element);
//                        }
////                        Set<OIdentifiable> policies = element.getProperty("policies");
//                    });
//
//            db.query("select from " + OrienteerUser.CLASS_NAME)
//                    .stream()
//                    .map(res -> res.getElement().get())
//                    .forEach(element -> {
//                        Set<OElement> read = element.getProperty("_allowRead");
//                        if (read != null && !read.isEmpty()) {
//                            LOG.info("[ADMIN] user: {} allowRead: {}", element, read.iterator().next());
//                        } else {
//                            LOG.info("[ADMIN] user: {}", element);
//                        }
//                    });
//        });

        DBClosure.sudoConsumer(db -> {
            LOG.info("record #4:1 {}", (ODocument) db.load(new ORecordId(4, 1)));
            LOG.info("record #4:2 {}", (ODocument) db.load(new ORecordId(4, 2)));
            LOG.info("record #4:0 {}", (ODocument) db.load(new ORecordId(4, 0)));

        });

        ODatabaseDocument db = tester.getDatabase();
        LOG.info("current user: {}", db.getUser());

        db.getUser().getRoles().forEach(role -> LOG.info("user role: {}", role));

//        db.query("select from " + ORole.CLASS_NAME)
//                .stream()
//                .map(res -> res.getElement().get())
//                .forEach(element -> {
//                    LOG.info("[READER] role: {} perspective: {}", element, element.getProperty("perspective"));
//                });

        db.query("select from OSecurityPolicy")
                .stream()
                .map(res -> res.getElement().get())
                .forEach(element -> {
                    LOG.info("[READER] policy: {}", element);
                });
    }

    @Test
    public void testRestorePassword() {
        final String password = UUID.randomUUID().toString();
        openRestorePage();
        fillAndSubmitRestorePasswordPanel(password);
        Page lastRenderedPage = tester.getLastRenderedPage();
        LOG.info("lastRenderedPage: {}", lastRenderedPage);
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
        tester.startPage(OUsersLoginPage.class);
        FormTester formTester = tester.newFormTester("container:loginPanel:form");
        formTester.setValue("username", testUser.getName());
        formTester.setValue("password", password);

        tester.clickLink("container:loginPanel:form:loginButtonsPanel:loginButton:command", true);

        OrienteerWebSession session = (OrienteerWebSession) tester.getSession();

        ODatabaseDocumentInternal database = session.getDatabase();
        ORID userRid = database.getUser().getDocument().getIdentity();

        Set<OIdentifiable> allowRead = database.getUser().getDocument().field("_allowRead");

        allowRead.forEach(read -> LOG.info("allow read: {}", read));


        DBClosure.sudoConsumer(db -> {
            ODocument userDoc = db.load(userRid);
            Set<OIdentifiable> allowReadAdm = userDoc.field("_allowRead");

            allowReadAdm.stream().map(rid -> (ODocument) rid.getRecord())
                    .forEach(read -> {
                        if (read.getSchemaClass().getName().equalsIgnoreCase("ORole")) {
                            Set<OIdentifiable> allowReadAdmR = read.field("_allowRead");
                            allowReadAdmR.forEach(r -> LOG.info("[admin] [{}] allow read: {}", read.getIdentity(), r));
                        }
                        LOG.info("[admin] allow read: {}", read);
                    });
        });



//        LOG.info("session effective user: {}", session.getEffectiveUser());
//        LOG.info("current db user: {}", database.getUser().getDocument().getIdentity());
//        LOG.info("session user: {}", session.getUserModel().getOrid());
        LOG.info("loaded db user: {}", (ODocument) database.load(userRid));

//        DBClosure.sudoConsumer(db -> {
//            LOG.info("#9:0 {}", (ODocument) db.load(new ORecordId(9, 0)));
//        });


        assertNotNull(session.getUser());
        assertEquals(testUser.getName(), session.getUser().getName());
    }
}
