package org.orienteer.logger.server;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orienteer.core.dao.DAO;
import org.orienteer.core.util.CommonUtils;
import org.orienteer.junit.OrienteerTestRunner;
import org.orienteer.logger.OLogger;
import org.orienteer.logger.server.model.IOCorrelationIdGeneratorModel;
import org.orienteer.logger.server.model.IOLoggerDAO;
import org.orienteer.logger.server.model.IOLoggerEventDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventMailDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventModel;
import org.orienteer.logger.server.service.OTestLoggerMailService;
import org.orienteer.logger.server.service.dispatcher.OLoggerEventMailDispatcher;
import org.orienteer.mail.model.OMail;
import org.orienteer.mail.model.OMailSettings;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.service.IOMailService;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(OrienteerTestRunner.class)
public class TestOLoggerEventMailDispatcher {

    private OMail mail;
    private OMailSettings settings;
    private IOLoggerEventMailDispatcherModel mailDispatcher;
    private IOCorrelationIdGeneratorModel correlationIdGenerator;

    private Exception exception1;
    private Exception exception2;

    @Inject
    private IOMailService mailService;
    
    @Inject
    private IOLoggerDAO loggerDAO;

    @Before
    public void init() {
        DBClosure.sudoConsumer(db -> {
            settings = createMailSettings();
            settings.save();

            mail = createMail(settings);
            mail.save();
            mail.reload();

            mailDispatcher = createMailDispatcherModel(mail);
            mailDispatcher.save();

            correlationIdGenerator = loggerDAO.getOCorrelationIdGenerator(OLoggerModule.CORRELATION_ID_GENERATOR_ORIENTEER);
            if(correlationIdGenerator==null) throw new IllegalStateException("There is no correlation id generator with alias: " + OLoggerModule.CORRELATION_ID_GENERATOR_ORIENTEER);

            OLoggerModule.ILoggerModuleConfiguration module = OLoggerModule.ILoggerModuleConfiguration.get();
            module.setCorrelationIdGenerator(correlationIdGenerator);
            module.setLoggerEventDispatcher(mailDispatcher);
            module.setActivated(true);
            module.save();
        });

        exception1 = new TestException(UUID.randomUUID().toString());
        exception2 = new SecondTestException(UUID.randomUUID().toString());
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db -> {
            db.delete(mailDispatcher.getDocument());
            db.delete(mail.getDocument());
            db.delete(settings.getDocument());
            deleteEvents(db, exception1);
            deleteEvents(db, exception2);

            IOLoggerEventDispatcherModel dispatcher = loggerDAO.getOLoggerEventDispatcher(OLoggerModule.DISPATCHER_DEFAULT);
            if(dispatcher!=null) OLoggerModule.ILoggerModuleConfiguration.get()
                                    .setLoggerEventDispatcher(dispatcher)
                                    .setActivated(false)
                                    .save();
        });
    }

    private void deleteEvents(ODatabaseDocument db, Exception exception) {
        loggerDAO.getEventsByCorrelationId(correlationIdGenerator.createCorrelationIdGenerator().generate(exception))
                .stream()
                .map(IOLoggerEventModel::getDocument)
                .forEach(db::delete);
    }

    private IOLoggerEventMailDispatcherModel createMailDispatcherModel(OMail mail) {
        Set<String> recipients = new HashSet<>();
        recipients.add("test1@gmail.com");
        recipients.add("test2@gmail.com");


        Set<String> exceptions = new HashSet<>();
        exceptions.add(TestException.class.getName());

        IOLoggerEventMailDispatcherModel dispatcher = DAO.create(IOLoggerEventMailDispatcherModel.class);

        dispatcher.setAlias(UUID.randomUUID().toString());
        dispatcher.setDispatcherClass(OLoggerEventMailDispatcher.class.getName());
        dispatcher.setName(CommonUtils.toMap("en", "test"));
        dispatcher.setExceptions(exceptions);
        dispatcher.setRecipients(recipients);
        dispatcher.setMail(mail);

        return dispatcher;
    }

    private OMailSettings createMailSettings() {
        OMailSettings settings = new OMailSettings();
        settings.setEmail("test@gmail.com");
        settings.setImapHost("imap.gmail.com");
        settings.setImapPort(993);
        settings.setSmtpHost("smtp.gmail.com");
        settings.setSmtpPort(587);
        return settings;
    }

    private OMail createMail(OMailSettings settings) {
        OMail mail = new OMail();
        mail.setFrom("test-logger@gmail.com");
        mail.setMailSettings(settings);
        mail.setText("Test logger");
        mail.setSubject("Test logger");
        return mail;
    }



    @Test
    public void testSendingEventByMail() {
        OLogger.log(exception1);

        String correlationId = correlationIdGenerator.createCorrelationIdGenerator().generate(exception1);

        List<IOLoggerEventModel> events = loggerDAO.getEventsByCorrelationId(correlationId);
        assertEquals("There is no events with correlationId: " + correlationId, 1, events.size());
        assertEquals("Seed classes are not equals", exception1.getClass().getName(), events.get(0).getSeedClass());

        OPreparedMail preparedMail = ((OTestLoggerMailService) mailService).getMails().pop();

        assertEquals("Mail templates are not equals", mail.getDocument().getIdentity(), preparedMail.getMailTemplate().getDocument().getIdentity());
        assertEquals("Mail recipients are not equals", mailDispatcher.getRecipients(), new HashSet<>(preparedMail.getRecipients()));
    }

    @Test
    public void testIgnoreSendingEventByMail() {
        OLogger.log(exception2);

        String correlationId = correlationIdGenerator.createCorrelationIdGenerator().generate(exception2);
        List<IOLoggerEventModel> events = loggerDAO.getEventsByCorrelationId(correlationId);
        assertTrue("There is present event with correlationId: " + correlationId, events.isEmpty());

        List<OPreparedMail> mails = ((OTestLoggerMailService) mailService).getMails();
        assertTrue("Mails are not empty", mails.isEmpty());
    }

    private static class TestException extends IllegalStateException {
        public TestException(String s) {
            super(s);
        }
    }

    private static class SecondTestException extends IllegalStateException {
        public SecondTestException(String s) {
            super(s);
        }
    }
}
