package org.orienteer.users.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.schedule.OScheduledEvent;
import com.orientechnologies.orient.core.schedule.OScheduledEventBuilder;
import com.orientechnologies.orient.core.schedule.OScheduler;
import org.apache.wicket.markup.html.WebPage;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.model.OPreparedMail;
import org.orienteer.service.IOMailService;
import org.orienteer.users.model.OrienteerUser;
import org.orienteer.users.resource.RegistrationResource;
import org.orienteer.users.resource.RestorePasswordResource;
import org.orienteer.users.web.DefaultRegistrationPage;
import org.orienteer.users.web.DefaultRestorePasswordPage;
import org.orienteer.util.OMailUtils;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.orienteer.users.module.OrienteerUsersModule.*;

@Singleton
public class OrienteerUsersService implements IOrienteerUsersService {

    @Inject
    private IOMailService mailService;

    @Override
    public void restoreUserPassword(OrienteerUser user) {
        clearRestoring(user); // clear previous restore data if it present

        DBClosure.sudoConsumer(db -> {
            user.setRestoreId(UUID.randomUUID().toString())
                    .setRestoreIdCreated(Instant.now())
                    .save();
            String name = EVENT_RESTORE_PASSWORD_PREFIX + user.getRestoreId();
            OScheduler scheduler = db.getMetadata().getScheduler();
            scheduler.scheduleEvent(createRestorePasswordSchedulerEvent(db, user, name));
        });
        notifyUserAboutRestorePassword(user);
    }

    @Override
    public void clearRestoring(OrienteerUser user) {
        if (user.getRestoreId() != null) {
            DBClosure.sudoConsumer(db -> {
                String eventName = EVENT_RESTORE_PASSWORD_PREFIX + user.getRestoreId();
                OScheduler scheduler = db.getMetadata().getScheduler();
                if (scheduler.getEvent(eventName) != null) {
                    scheduler.removeEvent(eventName);
                }
                user.setRestoreId(null);
                user.setRestoreIdCreatedAsDate(null);
                user.save();
            });
        }
    }

    @Override
    public void notifyUserAboutRegistration(OrienteerUser user) {
        OMailUtils.getOMailByName(MAIL_REGISTRATION)
                .ifPresent(mail -> {
                    OPreparedMail preparedMail = new OPreparedMail(mail, createRegistrationMailMacros(user));
                    adjustRegistrationPreparedMail(preparedMail, user);
                    DBClosure.sudoSave(preparedMail);
                    mailService.sendMailAsync(preparedMail);
                });
    }

    @Override
    public Class<? extends WebPage> getRestorePasswordPage() {
        return DefaultRestorePasswordPage.class;
    }

    @Override
    public Class<? extends WebPage> getRegistrationPage() {
        return DefaultRegistrationPage.class;
    }

    protected void notifyUserAboutRestorePassword(OrienteerUser user) {
        OMailUtils.getOMailByName(MAIL_RESTORE)
                .ifPresent(mail -> {
                    OPreparedMail preparedMail = new OPreparedMail(mail, createRestoreMailMacros(user));
                    adjustRestorePreparedMail(preparedMail, user);
                    DBClosure.sudoSave(preparedMail);
                    mailService.sendMailAsync(preparedMail);
                });
    }

    protected Map<String, Object> createRestoreMailMacros(OrienteerUser user) {
        return CommonUtils.toMap(MAIL_MACROS_LINK, RestorePasswordResource.getLinkForUser(user));
    }

    protected Map<String, Object> createRegistrationMailMacros(OrienteerUser user) {
        return CommonUtils.toMap(MAIL_MACROS_LINK, RegistrationResource.createRegistrationLink(user));
    }

    protected void adjustRestorePreparedMail(OPreparedMail preparedMail, OrienteerUser user) {
        preparedMail.addRecipient(user.getEmail());
    }

    protected void adjustRegistrationPreparedMail(OPreparedMail preparedMail, OrienteerUser user) {
        preparedMail.addRecipient(user.getEmail());
    }

    private OScheduledEvent createRestorePasswordSchedulerEvent(ODatabaseDocument db, OrienteerUser user, String eventName) {
        OProperty property = user.getDocument().getSchemaClass().getProperty(OrienteerUser.PROP_RESTORE_ID);
        OFunction fun = db.getMetadata().getFunctionLibrary().getFunction(FUN_REMOVE_RESTORE_ID);
        long timeout = Long.parseLong(REMOVE_SCHEDULE_START_TIMEOUT.getValue(property));
        return new OScheduledEventBuilder()
                .setName(eventName)
                .setFunction(fun)
                .setArguments(createFunArguments(user, timeout, eventName))
                .setRule(REMOVE_CRON_RULE.getValue(property))
                .setStartTime(new Date(System.currentTimeMillis() + timeout)).build();
    }

    private Map<Object, Object> createFunArguments(OrienteerUser user, long timeout, String eventName) {
        return CommonUtils.toMap(
                PARAM_RESTORE_ID, user.getRestoreId(),
                PARAM_TIMEOUT, timeout,
                PARAM_EVENT_NAME, eventName
        );
    }
}
