package org.orienteer.mail.task;

import com.google.inject.ProvidedBy;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.dao.DAOOClass;
import org.orienteer.core.dao.ODocumentWrapperProvider;
import org.orienteer.core.tasks.IOTask;
import org.orienteer.core.tasks.IOTaskSessionPersisted;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.mail.model.OPreparedMail;
import org.orienteer.mail.service.IOMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ydn.wicket.wicketorientdb.utils.DBClosure;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Task which sends list of {@link OPreparedMail} from prepared {@link IOSendMailTaskSession}.
 * Can be used for prepare mails during application lifecycle and send batch of mails in one place via this task
 */
@ProvidedBy(ODocumentWrapperProvider.class)
@DAOOClass(value = IOSendMailTask.CLASS_NAME, orderOffset = 50)
public interface IOSendMailTask extends IOTask {

    public static final Logger LOG = LoggerFactory.getLogger(IOSendMailTask.class);

    public static final String CLASS_NAME = "OSendMailTask";

    @Override
    public default OTaskSessionRuntime<IOSendMailTaskSession> startNewSession() {
    	throw new IllegalStateException("You should precreate "+IOSendMailTaskSession.class.getSimpleName()+" first");
    }
    public default OTaskSessionRuntime<IOSendMailTaskSession> startNewSession(IOSendMailTaskSession session) {
    	OTaskSessionRuntime<IOSendMailTaskSession> runtime = new OTaskSessionRuntime<>(IOSendMailTaskSession.class)
    															.init(this);
        runtime.start().setProgress(0);

        performTask(runtime);
        return runtime;
    }

    public default void performTask(OTaskSessionRuntime<IOSendMailTaskSession> runtime) {
        OrienteerWebSession session = OrienteerWebSession.get();
        OrienteerWebApplication app = OrienteerWebApplication.get();
        RequestCycle requestCycle = RequestCycle.get();

        new Thread(() -> {
            ThreadContext.setSession(session);
            ThreadContext.setApplication(app);
            ThreadContext.setRequestCycle(requestCycle);

            DBClosure.sudoConsumer(db -> {
                try {
                    sendMails(runtime);
                } catch (Exception ex) {
                    LOG.error("Error occurred during perform task {}", IOSendMailTask.this, ex);
                } finally {
                    runtime.finish();
                }
            });
        }).start();
    }

    static void sendMails(OTaskSessionRuntime<IOSendMailTaskSession> runtime) throws UnsupportedEncodingException, MessagingException {
        List<OPreparedMail> mails = runtime.getOTaskSessionPersisted().getMails();
        IOMailService service = OrienteerWebApplication.lookupApplication().getServiceInstance(IOMailService.class);
        final int allSize = mails.size();
        int counter = 1;

        for (OPreparedMail mail : mails) {
            service.sendMail(mail);
            runtime.setProgress(100.0 * counter / allSize);
            counter++;
        }
    }
}
