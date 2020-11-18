package org.orienteer.users;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.function.OFunctionLibrary;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.schedule.OScheduledEvent;
import com.orientechnologies.orient.core.schedule.OScheduledEventBuilder;
import com.orientechnologies.orient.core.schedule.OScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.junit.OrienteerTester;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.service.IOrienteerUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(OrienteerTestRunner.class)
public class RestorePasswordTest {

    private static Logger LOG = LoggerFactory.getLogger(RestorePasswordTest.class);
    private static final String TEST_RESTORE_FUNCTION = "TestRestorePasswordFunction";

    @Inject
    private IOrienteerUsersService usersService;
    
    @Inject
    private OrienteerTester tester;

    private OrienteerUser user;

    @Before
    public void init() {
        DBClosure.sudoConsumer(db -> {
            user = new OrienteerUser(OUser.CLASS_NAME);
            user.setName(UUID.randomUUID().toString())
                    .setPassword(UUID.randomUUID().toString())
                    .setAccountStatus(OSecurityUser.STATUSES.ACTIVE);
            user.setEmail(UUID.randomUUID().toString() + "@gmail.com");
            user.save();

            OProperty property = user.getDocument().getSchemaClass().getProperty(OrienteerUser.PROP_RESTORE_ID);
            OrienteerUsersModule.REMOVE_CRON_RULE.setValue(property, "0/7 0/1 * 1/1 * ? *");
            OrienteerUsersModule.REMOVE_SCHEDULE_START_TIMEOUT.setValue(property, "3000");
        });
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db -> {
            	//Restore default
            	OProperty property = user.getDocument().getSchemaClass().getProperty(OrienteerUser.PROP_RESTORE_ID);
            	OrienteerUsersModule.REMOVE_CRON_RULE.setValue(property, "0 0/1 * * * ?");
            	OrienteerUsersModule.REMOVE_SCHEDULE_START_TIMEOUT.setValue(property, "86400000");

            	db.delete(user.getDocument().getIdentity());
        	}
        );
        tester.signOut();
    }

    @Test
    public void testRestorePassword() throws InterruptedException {
        usersService.restoreUserPassword(user);
        OScheduledEvent event = getRestoreSchedulerEvent(user.getRestoreId());
        LOG.info("1. event = {}", event);

        LOG.info("user restore id: {}", user.getRestoreId());
        assertNotNull(event);
        Thread.sleep(20_000);

        LOG.info("user restore id: {}", user.getRestoreId());

        event = getRestoreSchedulerEvent(user.getRestoreId());
        LOG.info("2. event = {}", event);
        assertNull(event);
        LOG.info("Test");
    }

    @Test
    @Ignore
    /**
     * TODO: Rework later. Right now this test do almost nothing
     * @throws InterruptedException
     */
    public void testUpdate() throws InterruptedException {

        LOG.info("Start");

        String name = user.getName();


        DBClosure.sudoConsumer(db -> {

            OFunctionLibrary functionLibrary = db.getMetadata().getFunctionLibrary();

            OFunction test = functionLibrary.getFunction(TEST_RESTORE_FUNCTION);
            if(test==null) {
	            test = functionLibrary.createFunction(TEST_RESTORE_FUNCTION);
	
	            test.setLanguage("JavaScript");
	            test.setCode("print(\"Start execute\");var res = db.command(\"update OUser set firstName='Test' where name = ?\", name);print(res.next());");
	            test.setParameters(Collections.singletonList("name"));
	            test.save();
            }

            OScheduler scheduler = db.getMetadata().getScheduler();
            OScheduledEvent event = new OScheduledEventBuilder()
                    .setName("test-event")
                    .setFunction(test)
                    .setArguments(CommonUtils.toMap("name", name))
                    .setRule("0 0/1 * 1/1 * ? *")
                    .setStartTime(new Date(System.currentTimeMillis() + 2000)).build();

            scheduler.scheduleEvent(event);
        });



        Thread.sleep(10_000);
        LOG.info("Finish");
    }


    private OScheduledEvent getRestoreSchedulerEvent(String id) {
        return DBClosure.sudo(db -> {
            OScheduler scheduler = db.getMetadata().getScheduler();
            OScheduledEvent event = scheduler.getEvent(OrienteerUsersModule.EVENT_RESTORE_PASSWORD_PREFIX + id);
            return event;
        });
    }
}
