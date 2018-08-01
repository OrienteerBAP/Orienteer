package org.orienteer.task;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.orienteer.core.OrienteerWebApplication;
import org.orienteer.core.OrienteerWebSession;
import org.orienteer.core.tasks.OTask;
import org.orienteer.core.tasks.OTaskSessionRuntime;
import org.orienteer.model.OPreparedMail;
import org.orienteer.service.IOMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Task which sends list of {@link OPreparedMail} from prepared {@link OSendMailTaskSession}.
 * Can be used for prepare mails during application lifecycle and send batch of mails in one place via this task
 */
public class OSendMailTask extends OTask {

    private static final Logger LOG = LoggerFactory.getLogger(OSendMailTask.class);

    private final OSendMailTaskSession session;

    /**
     * Constructor
     * @param oTask {@link ODocument} document of task
     * @param session {@link OSendMailTaskSession} prepared session for use in task
     */
    public OSendMailTask(ODocument oTask, OSendMailTaskSession session) {
        super(oTask);
        this.session = session;
    }

    @Override
    public OTaskSessionRuntime startNewSession() {
        OSendMailTaskSessionRuntime runtime = new OSendMailTaskSessionRuntime(session);
        runtime.setDeleteOnFinish(isAutodeleteSessions());
        performTask(runtime);
        return runtime;
    }

    private void performTask(OSendMailTaskSessionRuntime runtime) {
        OrienteerWebSession session = OrienteerWebSession.get();
        OrienteerWebApplication app = OrienteerWebApplication.get();
        RequestCycle requestCycle = RequestCycle.get();

        new Thread(() -> {
            try {
                ThreadContext.setSession(session);
                ThreadContext.setApplication(app);
                ThreadContext.setRequestCycle(requestCycle);
                runtime.start();
                runtime.setProgress(0);
                sendMails(runtime);
            } catch (Exception ex) {
                LOG.error("Error occurred during perform task {}", OSendMailTask.this, ex);
            } finally {
                runtime.finish();
            }
        }).start();
    }

    private void sendMails(OSendMailTaskSessionRuntime runtime) throws UnsupportedEncodingException, MessagingException {
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
