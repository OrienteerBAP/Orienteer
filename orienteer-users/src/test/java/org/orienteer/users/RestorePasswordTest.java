package org.orienteer.users;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.schedule.OScheduledEvent;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.users.module.OrienteerUsersModule;
import org.orienteer.users.service.IOrienteerUsersService;
import org.orienteer.users.model.OrienteerUser;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

@RunWith(OrienteerTestRunner.class)
public class RestorePasswordTest {

    @Inject
    private IOrienteerUsersService usersService;

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
            OrienteerUsersModule.REMOVE_CRON_RULE.setValue(property, "0 0/1 * 1/1 * ? *");
            OrienteerUsersModule.REMOVE_SCHEDULE_START_TIMEOUT.setValue(property, "3000");
        });
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db ->
            db.command(new OCommandSQL("delete from ?")).execute(user.getDocument())
        );
    }

    @Test
    public void testRestorePassword() throws InterruptedException {
        usersService.restoreUserPassword(user);
        assertNotNull(getRestoreSchedulerEvent(user.getRestoreId()));
        Thread.currentThread().join(70_000);
        assertNull(getRestoreSchedulerEvent(user.getRestoreId()));
    }

    private OScheduledEvent getRestoreSchedulerEvent(String id) {
        return DBClosure.sudo(db -> db.getMetadata().getScheduler().getEvent(OrienteerUsersModule.EVENT_RESTORE_PASSWORD_PREFIX + id));
    }
}
