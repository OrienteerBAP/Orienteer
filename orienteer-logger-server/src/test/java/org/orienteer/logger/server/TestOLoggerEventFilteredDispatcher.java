package org.orienteer.logger.server;

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
import org.orienteer.logger.server.model.IOLoggerEventFilteredDispatcherModel;
import org.orienteer.logger.server.model.IOLoggerEventModel;
import org.orienteer.logger.server.service.dispatcher.OLoggerEventFilteredDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(OrienteerTestRunner.class)
public class TestOLoggerEventFilteredDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(TestOLoggerEventFilteredDispatcher.class);

    private IOLoggerEventFilteredDispatcherModel filteredDispatcher;
    private IOCorrelationIdGeneratorModel correlationIdGenerator;
    
    @Inject
    private IOLoggerDAO loggerDAO;

    @Before
    public void init() {
        DBClosure.sudoConsumer(db -> {
            Set<String> exceptions = new HashSet<>();
            exceptions.add(TestException.class.getName());

            filteredDispatcher = DAO.create(IOLoggerEventFilteredDispatcherModel.class);
            filteredDispatcher.setAlias(UUID.randomUUID().toString());
            filteredDispatcher.setDispatcherClass(OLoggerEventFilteredDispatcher.class.getName());
            filteredDispatcher.setName(CommonUtils.toMap("en", "test"));
            filteredDispatcher.setExceptions(exceptions);
            filteredDispatcher.save();

            correlationIdGenerator = loggerDAO.getOCorrelationIdGenerator(OLoggerModule.CORRELATION_ID_GENERATOR_ORIENTEER);
            if(correlationIdGenerator==null) new IllegalStateException("There is no correlation id generator with id: " + OLoggerModule.CORRELATION_ID_GENERATOR_ORIENTEER);

            OLoggerModule.ILoggerModuleConfiguration module = OLoggerModule.ILoggerModuleConfiguration.get();
            module.setLoggerEventDispatcher(filteredDispatcher);
            module.setCorrelationIdGenerator(correlationIdGenerator);
            module.setActivated(true);
            module.save();
        });
    }

    @After
    public void destroy() {
        DBClosure.sudoConsumer(db -> {
            db.delete(filteredDispatcher.getDocument());

            db.command("delete from " + IOLoggerEventModel.CLASS_NAME);

            IOLoggerEventDispatcherModel dispatcher = loggerDAO.getOLoggerEventDispatcher(OLoggerModule.DISPATCHER_DEFAULT);
            if(dispatcher!=null) {
            		OLoggerModule.ILoggerModuleConfiguration.get()
                                .setLoggerEventDispatcher(dispatcher)
                                .setActivated(false)
                                .save();
            }
        });
    }

    @Test
    public void testDispatchEvent() {
        TestException exception = new TestException("Test Exception: " + UUID.randomUUID().toString());
        OLogger.log(exception);

        String correlationId = correlationIdGenerator.createCorrelationIdGenerator().generate(exception);

        List<IOLoggerEventModel> events = loggerDAO.getEventsByCorrelationId(correlationId);
        assertEquals("There is no events with correlationId: " + correlationId, 1, events.size());
        assertEquals("Seed classes are not equals", exception.getClass().getName(), events.get(0).getSeedClass());
    }

    @Test
    public void testIgnoreEvent() {
        SecondTestException exception = new SecondTestException("Test Exception: " + UUID.randomUUID().toString());
        OLogger.log(exception);

        String correlationId = correlationIdGenerator.createCorrelationIdGenerator().generate(exception);
        List<IOLoggerEventModel> events = loggerDAO.getEventsByCorrelationId(correlationId);
        assertTrue("There is present event with correlationId: " + correlationId, events.isEmpty());
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
